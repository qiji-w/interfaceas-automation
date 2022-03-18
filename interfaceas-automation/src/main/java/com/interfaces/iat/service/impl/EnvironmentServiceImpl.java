package com.interfaces.iat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.enviroment.EnvironmentCreateInputDto;
import com.interfaces.iat.dto.input.enviroment.EnvironmentUpdateInputDto;
import com.interfaces.iat.dto.output.enviroment.EnvironmentOutputDto;
import com.interfaces.iat.dto.output.module.ModuleOutputDto;
import com.interfaces.iat.entity.EnvironmentEntity;
import com.interfaces.iat.entity.ProjectEntity;
import com.interfaces.iat.mapper.EnvironmentMapper;
import com.interfaces.iat.service.EnvironmentService;
import com.interfaces.iat.service.ProjectService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class EnvironmentServiceImpl extends ServiceImpl<EnvironmentMapper, EnvironmentEntity> implements EnvironmentService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ProjectService projectService;


    @Override
    public ResponseData<List <EnvironmentOutputDto>> queryByProjectId(Integer projectId) {
        ResponseData<List<EnvironmentOutputDto>> responseData;

        try {
            QueryWrapper<EnvironmentEntity> queryWrapper = new QueryWrapper<>();
            if(projectId != null) {
                queryWrapper.eq("project_id", projectId);
            }
            queryWrapper.eq("is_delete",false); //只取没有删除的
            queryWrapper.orderByDesc("id");
            List <EnvironmentEntity> entities = this.list(queryWrapper);
            List <EnvironmentOutputDto> outputDtos = entities.stream().map(s -> modelMapper.map(s, EnvironmentOutputDto.class)).collect(Collectors.toList());

            responseData = ResponseData.success(outputDtos);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData <EnvironmentOutputDto> getById(Integer id) {
        ResponseData<EnvironmentOutputDto> responseData;
        try {
            EnvironmentEntity entity = super.getById(id);

            EnvironmentOutputDto outputDto = modelMapper.map(entity,EnvironmentOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData <EnvironmentOutputDto> create(EnvironmentCreateInputDto inputDto) {
        ResponseData<EnvironmentOutputDto> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //模块名称是否存在
            QueryWrapper<EnvironmentEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", inputDto.getName());
            queryWrapper.eq("project_id",inputDto.getProjectId());
            queryWrapper.eq("is_delete",false);
            EnvironmentEntity environmentEntity = this.getOne(queryWrapper,false);
            if (environmentEntity!=null) {
                checkMsgs.add("环境名称已经存在");
            }
            //所属模块是否存在
            QueryWrapper<ProjectEntity> projectQueryWrapper = new QueryWrapper<>();
            projectQueryWrapper.eq("id", inputDto.getProjectId());
            projectQueryWrapper.eq("is_delete", false);
            queryWrapper.eq("project_id",inputDto.getProjectId());
            Integer existCount = projectService.count(projectQueryWrapper);
            if(existCount<=0){
                checkMsgs.add("所属项目不存在");
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            EnvironmentEntity entity = modelMapper.map(inputDto,EnvironmentEntity.class);
            entity.setIsDelete(false);
            this.save(entity);
            ModuleOutputDto outputDto = modelMapper.map(entity,ModuleOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData <EnvironmentOutputDto> update(EnvironmentUpdateInputDto inputDto) {
        ResponseData<EnvironmentOutputDto> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //模块名称是否存在
            QueryWrapper<EnvironmentEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", inputDto.getName());
            queryWrapper.eq("is_delete",false);
            queryWrapper.eq("project_id",inputDto.getProjectId());
            queryWrapper.ne("id",inputDto.getId());
            EnvironmentEntity environmentEntity = this.getOne(queryWrapper,false);
            if (environmentEntity!=null) {
                checkMsgs.add("环境名称已经存在");
            }
            //所属模块是否存在
            QueryWrapper<ProjectEntity> projectQueryWrapper = new QueryWrapper<>();
            projectQueryWrapper.eq("id", inputDto.getProjectId());
            projectQueryWrapper.eq("is_delete", false);
            Integer existCount = projectService.count(projectQueryWrapper);
            if(existCount<=0){
                checkMsgs.add("所属项目不存在");
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            EnvironmentEntity entity = modelMapper.map(inputDto,EnvironmentEntity.class);
            entity.setIsDelete(false);
            this.updateById(entity);
            ModuleOutputDto outputDto = modelMapper.map(entity,ModuleOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData <Boolean> delete(Integer id) {
        ResponseData<Boolean> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //项目名称是否存在
            QueryWrapper<EnvironmentEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",id);
            queryWrapper.eq("is_delete",false);
            EnvironmentEntity environmentEntity = this.getOne(queryWrapper,false);
            if (environmentEntity==null) {
                checkMsgs.add("项目环境不存在");
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            environmentEntity.setIsDelete(true);
            Boolean result = this.updateById(environmentEntity);

            responseData = ResponseData.success(result);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }
}
