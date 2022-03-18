package com.interfaces.iat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.project.ProjectCreateInputDto;
import com.interfaces.iat.dto.input.project.ProjectUpdateInputDto;
import com.interfaces.iat.dto.output.project.ProjectOutputDto;
import com.interfaces.iat.entity.ProjectEntity;
import com.interfaces.iat.mapper.ProjectMapper;
import com.interfaces.iat.service.ProjectService;
import com.interfaces.iat.util.SessionUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectEntity> implements ProjectService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    SessionUtil sessionUtil;

    @Override
    public ResponseData<List<ProjectOutputDto>> queryAll() {
        ResponseData<List<ProjectOutputDto>> responseData;

        try {
            List <ProjectEntity> entities = this.list();
            entities = entities.stream().filter(s->s.getIsDelete() == false).collect(Collectors.toList());
            List <ProjectOutputDto> outputDtos = entities.stream().sorted(Comparator.comparing(ProjectEntity::getId).reversed()) .map(s -> modelMapper.map(s, ProjectOutputDto.class)).collect(Collectors.toList());

            responseData = ResponseData.success(outputDtos);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<ProjectOutputDto> getById(Integer id) {
        ResponseData<ProjectOutputDto> responseData;
        try {
            ProjectEntity entity = super.getById(id);

            ProjectOutputDto outputDto = modelMapper.map(entity,ProjectOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<ProjectOutputDto> create(ProjectCreateInputDto inputDto) {
        ResponseData<ProjectOutputDto> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //项目名称是否存在
            QueryWrapper<ProjectEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", inputDto.getName());
            queryWrapper.eq("is_delete",false);
           ProjectEntity projectEntity = this.getOne(queryWrapper,false);
            if (projectEntity!=null) {
               checkMsgs.add("项目已经存在");
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            ProjectEntity entity = modelMapper.map(inputDto,ProjectEntity.class);
            entity.setIsDelete(false);
            this.save(entity);
            ProjectOutputDto outputDto = modelMapper.map(entity,ProjectOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<ProjectOutputDto> update(ProjectUpdateInputDto inputDto) {
        ResponseData<ProjectOutputDto> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //项目名称是否存在
            QueryWrapper<ProjectEntity> queryWrapperOfSameName = new QueryWrapper<ProjectEntity>();
            queryWrapperOfSameName.ne("id",inputDto.getId());
            queryWrapperOfSameName.eq("name", inputDto.getName());
            queryWrapperOfSameName.eq("is_delete",false);
            ProjectEntity sameNameProjectEntity = this.getOne(queryWrapperOfSameName,false);
            if (sameNameProjectEntity!=null) {
                checkMsgs.add("项目已经存在");
            }
            //项目id是否存在
            QueryWrapper<ProjectEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",inputDto.getId());
            queryWrapper.eq("is_delete",false);
            ProjectEntity projectEntity = this.getOne(queryWrapper,false);
            if (projectEntity!=null) {
                //只能删除自己的项目
                SessionUtil.CurrentUser currentUser = sessionUtil.getCurrentUser();
                if (projectEntity.getCreateById().intValue() != currentUser.getUserEntity().getId().intValue() && projectEntity.getLeaderId().intValue() != currentUser.getUserEntity().getId().intValue()) {
                    checkMsgs.add(String.format("项目[%s]的创建人和负责人才能修改该项目", projectEntity.getName()));
                }
            }else {
                checkMsgs.add("项目不存在或已经删除");
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            ProjectEntity entity = modelMapper.map(inputDto,ProjectEntity.class);
            this.updateById(entity);
            ProjectOutputDto outputDto = modelMapper.map(entity,ProjectOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<Boolean> delete(Integer id) {
        ResponseData<Boolean> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //项目id是否存在
            QueryWrapper<ProjectEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",id);
            queryWrapper.eq("is_delete",false);
            ProjectEntity projectEntity = this.getOne(queryWrapper,false);
            if (projectEntity!=null) {
                //只能删除自己的项目
                SessionUtil.CurrentUser currentUser = sessionUtil.getCurrentUser();
                if (projectEntity.getCreateById().intValue() != currentUser.getUserEntity().getId().intValue() && projectEntity.getLeaderId().intValue() != currentUser.getUserEntity().getId().intValue()) {
                    checkMsgs.add(String.format("项目[%s]的创建人和负责人才能删除该项目", projectEntity.getName()));
                }
            }else {
                checkMsgs.add("项目不存在或已经删除");
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            projectEntity.setIsDelete(true);
            Boolean result = this.updateById(projectEntity);

            responseData = ResponseData.success(result);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }
}
