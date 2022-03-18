package com.interfaces.iat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.inf.InterfaceCreateInputDto;
import com.interfaces.iat.dto.input.inf.InterfaceUpdateInputDto;
import com.interfaces.iat.dto.output.inf.InterfaceOutputDto;
import com.interfaces.iat.dto.output.inf.InterfaceQueryOutputDto;
import com.interfaces.iat.dto.output.module.ModuleOutputDto;
import com.interfaces.iat.entity.InterfaceEntity;
import com.interfaces.iat.entity.ModuleEntity;
import com.interfaces.iat.entity.TestCaseEntity;
import com.interfaces.iat.mapper.InterfaceMapper;
import com.interfaces.iat.service.InterfaceService;
import com.interfaces.iat.service.ModuleService;
import com.interfaces.iat.service.ProjectService;
import com.interfaces.iat.service.TestCaseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class InterfaceServiceImpl extends ServiceImpl<InterfaceMapper, InterfaceEntity> implements InterfaceService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ModuleService moduleService;
    @Autowired
    ProjectService projectService;
    @Autowired
    TestCaseService testCaseService;

    @Override
    public ResponseData<List<InterfaceQueryOutputDto>> query(Integer pageIndex, Integer pageSize, Integer moduleId, Integer projectId) {
        ResponseData<List<InterfaceQueryOutputDto>> responseData;

        try {
            QueryWrapper<InterfaceEntity> queryWrapper = new QueryWrapper<>();
            if(moduleId != null) {
                queryWrapper.eq("module_id", moduleId);
            }
            if(projectId != null) {
                queryWrapper.eq("project_id", projectId);
            }
            queryWrapper.eq("is_delete",false); //只取没有删除的
            queryWrapper.orderByDesc("id");
            IPage<InterfaceEntity> queryPage = new Page<>(pageIndex, pageSize);
            queryPage = this.page(queryPage,queryWrapper);
            List<InterfaceQueryOutputDto> interfaceQueryOutputDtos = queryPage.getRecords().stream().map(s->modelMapper.map(s, InterfaceQueryOutputDto.class)).collect(Collectors.toList());

            //获取关联模块
            List<Integer> moduleIds = interfaceQueryOutputDtos.stream().map(s->s.getModuleId()).collect(Collectors.toList());
            List<ModuleEntity> moduleEntities = moduleService.listByIds(moduleIds);
            interfaceQueryOutputDtos.forEach(s->{
                ModuleEntity moduleEntity = moduleEntities.stream().filter(t->t.getId().equals(s.getModuleId())).findFirst().orElse(null);
                if(moduleEntity!=null) {
                    s.setModule(modelMapper.map(moduleEntity, ModuleOutputDto.class));
                }
            });

            responseData = ResponseData.success(interfaceQueryOutputDtos);
            responseData.setTotal(queryPage.getTotal());
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<List<InterfaceOutputDto>> queryByProjectId(Integer projectId) {
        ResponseData<List<InterfaceOutputDto>> responseData;

        try {
            QueryWrapper<InterfaceEntity> queryWrapper = new QueryWrapper<>();
            if(projectId != null) {
                queryWrapper.eq("project_id", projectId);
            }
            queryWrapper.eq("is_delete",false); //只取没有删除的
            queryWrapper.orderByDesc("id");

            List <InterfaceEntity> entities = this.list(queryWrapper);
            List <InterfaceOutputDto> outputDtos = entities.stream().map(s -> modelMapper.map(s, InterfaceOutputDto.class)).collect(Collectors.toList());

            responseData = ResponseData.success(outputDtos);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData <InterfaceOutputDto> getById(Integer id) {
        ResponseData<InterfaceOutputDto> responseData;
        try {
            InterfaceEntity entity = super.getById(id);

            InterfaceOutputDto outputDto = modelMapper.map(entity,InterfaceOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<InterfaceOutputDto> create(InterfaceCreateInputDto inputDto) {
        ResponseData<InterfaceOutputDto> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //接口名称是否存在
            QueryWrapper<InterfaceEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", inputDto.getName());
            queryWrapper.eq("is_delete",false);
            queryWrapper.eq("project_id",inputDto.getProjectId());
            queryWrapper.eq("module_id",inputDto.getModuleId());
            InterfaceEntity interfaceEntity = this.getOne(queryWrapper,false);
            if (interfaceEntity!=null) {
                checkMsgs.add("接口名称已经存在");
            }
            //接口地址是否存在
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("path", inputDto.getPath());
            queryWrapper.eq("is_delete",false);
            queryWrapper.eq("project_id",inputDto.getProjectId());
            queryWrapper.eq("module_id",inputDto.getModuleId());
            interfaceEntity = this.getOne(queryWrapper,false);
            if (interfaceEntity!=null) {
                checkMsgs.add("接口地址已经存在");
            }
            //所属模块是否存在
            QueryWrapper<ModuleEntity> moduleQueryWrapper = new QueryWrapper<>();
            moduleQueryWrapper.eq("id", inputDto.getModuleId());
            moduleQueryWrapper.eq("is_delete", false);
            Integer existCount = moduleService.count(moduleQueryWrapper);
            if(existCount<=0){
                checkMsgs.add("所属模块不存在");
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            InterfaceEntity entity = modelMapper.map(inputDto,InterfaceEntity.class);
            entity.setIsDelete(false);
            this.save(entity);
            InterfaceOutputDto outputDto = modelMapper.map(entity,InterfaceOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<InterfaceOutputDto> update(InterfaceUpdateInputDto inputDto) {
        ResponseData<InterfaceOutputDto> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //接口名称是否存在
            QueryWrapper<InterfaceEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", inputDto.getName());
            queryWrapper.eq("is_delete",false);
            queryWrapper.eq("project_id",inputDto.getProjectId());
            queryWrapper.eq("module_id",inputDto.getModuleId());
            queryWrapper.ne("id",inputDto.getId());
            InterfaceEntity interfaceEntity = this.getOne(queryWrapper,false);
            if (interfaceEntity!=null) {
                checkMsgs.add("接口名称已经存在");
            }
            //接口地址是否存在
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("path", inputDto.getPath());
            queryWrapper.eq("is_delete",false);
            queryWrapper.eq("project_id",inputDto.getProjectId());
            queryWrapper.eq("module_id",inputDto.getModuleId());
            queryWrapper.ne("id",inputDto.getId());
            interfaceEntity = this.getOne(queryWrapper,false);
            if (interfaceEntity!=null) {
                checkMsgs.add("接口地址已经存在");
            }
            //所属模块是否存在
            QueryWrapper<ModuleEntity> moduleQueryWrapper = new QueryWrapper<>();
            moduleQueryWrapper.eq("id", inputDto.getModuleId());
            moduleQueryWrapper.eq("is_delete", false);
            Integer existCount = moduleService.count(moduleQueryWrapper);
            if(existCount<=0){
                checkMsgs.add("所属模块不存在");
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            InterfaceEntity entity = modelMapper.map(inputDto,InterfaceEntity.class);
            entity.setIsDelete(false);
            this.updateById(entity);
            InterfaceOutputDto outputDto = modelMapper.map(entity,InterfaceOutputDto.class);

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
            QueryWrapper<InterfaceEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",id);
            queryWrapper.eq("is_delete",false);
            InterfaceEntity interfaceEntity = this.getOne(queryWrapper,false);
            if (interfaceEntity!=null) {
                QueryWrapper<TestCaseEntity> testCaseEntityQueryWrapper = new QueryWrapper<>();
                testCaseEntityQueryWrapper.eq("interface_id",id);
                testCaseEntityQueryWrapper.eq("is_delete",false);
                List<TestCaseEntity> testCaseEntities = testCaseService.list(testCaseEntityQueryWrapper);
                if(testCaseEntities!=null && testCaseEntities.size()>0){
                    checkMsgs.add("接口已经在测试套件的用例中被引用，不能删除");
                }
            }else {
                checkMsgs.add("接口不存在");
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            interfaceEntity.setIsDelete(true);
            Boolean result = this.updateById(interfaceEntity);

            responseData = ResponseData.success(result);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }
}
