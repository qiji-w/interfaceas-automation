package com.interfaces.iat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interfaces.iat.service.TaskModuleService;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.module.ModuleCreateInputDto;
import com.interfaces.iat.dto.output.module.ModuleOutputDto;
import com.interfaces.iat.dto.input.module.ModuleUpdateInputDto;
import com.interfaces.iat.entity.ModuleEntity;
import com.interfaces.iat.entity.ProjectEntity;
import com.interfaces.iat.entity.TaskModuleEntity;
import com.interfaces.iat.mapper.ModuleMapper;
import com.interfaces.iat.service.ModuleService;
import com.interfaces.iat.service.ProjectService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ModuleServiceImpl extends ServiceImpl<ModuleMapper, ModuleEntity> implements ModuleService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ProjectService projectService;
    @Autowired
    TaskModuleService taskModuleService;

    @Override
    public ResponseData<List<ModuleOutputDto>> query(Integer pageIndex, Integer pageSize, Integer projectId) {
        ResponseData<List<ModuleOutputDto>> responseData;

        try {
            QueryWrapper<ModuleEntity> queryWrapper = new QueryWrapper<>();
            if(projectId != null) {
                queryWrapper.eq("project_id", projectId);
            }
            queryWrapper.eq("is_delete",false);
            queryWrapper.orderByDesc("id");
            IPage<ModuleEntity> queryPage = new Page<>(pageIndex, pageSize);
            queryPage = this.page(queryPage,queryWrapper);

            responseData = ResponseData.success(queryPage.getRecords().stream().map(s->modelMapper.map(s, ModuleOutputDto.class)));
            responseData.setTotal(queryPage.getTotal());
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<List<ModuleOutputDto>> queryByProjectId(Integer projectId) {
        ResponseData<List<ModuleOutputDto>> responseData;

        try {
            QueryWrapper<ModuleEntity> queryWrapper = new QueryWrapper<>();
            if(projectId != null) {
                queryWrapper.eq("project_id", projectId);
            }
            queryWrapper.eq("is_delete",false); //只取没有删除的
            queryWrapper.orderByDesc("id");
            List <ModuleEntity> entities = this.list(queryWrapper);
            List <ModuleOutputDto> outputDtos = entities.stream().map(s -> modelMapper.map(s, ModuleOutputDto.class)).collect(Collectors.toList());

            responseData = ResponseData.success(outputDtos);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData <ModuleOutputDto> getById(Integer id) {
        ResponseData<ModuleOutputDto> responseData;
        try {
            ModuleEntity entity = super.getById(id);

            ModuleOutputDto outputDto = modelMapper.map(entity,ModuleOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<ModuleOutputDto> create(ModuleCreateInputDto inputDto) {
        ResponseData<ModuleOutputDto> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //当前项目模块名称是否存在
            QueryWrapper<ModuleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", inputDto.getName());
            queryWrapper.eq("is_delete",false);
            queryWrapper.eq("project_id",inputDto.getProjectId());
            ModuleEntity moduleEntity = this.getOne(queryWrapper,false);
            if (moduleEntity!=null) {
                checkMsgs.add("模块名称已经存在");
            }
            //所属项目是否存在
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

            ModuleEntity entity = modelMapper.map(inputDto,ModuleEntity.class);
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
    public ResponseData<ModuleOutputDto> update(ModuleUpdateInputDto inputDto) {
        ResponseData<ModuleOutputDto> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //模块名称是否存在
            QueryWrapper<ModuleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", inputDto.getName());
            queryWrapper.eq("is_delete",false);
            queryWrapper.eq("project_id",inputDto.getProjectId());
            queryWrapper.ne("id",inputDto.getId());
            ModuleEntity moduleEntity = this.getOne(queryWrapper,false);
            if (moduleEntity!=null) {
                checkMsgs.add("模块名称已经存在");
            }
            //所属项目是否存在
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

            ModuleEntity entity = modelMapper.map(inputDto,ModuleEntity.class);
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
    public ResponseData<Boolean> delete(Integer id) {
        ResponseData<Boolean> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //项目名称是否存在
            QueryWrapper<ModuleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",id);
            queryWrapper.eq("is_delete",false);
            ModuleEntity moduleEntity = this.getOne(queryWrapper,false);
            if (moduleEntity!=null) {
                //如果模块被关联到测试任务，则不能删除
                QueryWrapper<TaskModuleEntity> taskModuleEntityQueryWrapper = new QueryWrapper<>();
                taskModuleEntityQueryWrapper.eq("module_id",id);
                List<TaskModuleEntity> taskModuleEntities = taskModuleService.list(taskModuleEntityQueryWrapper);
                if(taskModuleEntities!=null && taskModuleEntities.size()>0){
                    checkMsgs.add("模块已经在测试任务中被引用，不能删除");
                }
            }else{
                checkMsgs.add("模块不存在");
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            moduleEntity.setIsDelete(true);
            Boolean result = this.updateById(moduleEntity);

            responseData = ResponseData.success(result);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }
}
