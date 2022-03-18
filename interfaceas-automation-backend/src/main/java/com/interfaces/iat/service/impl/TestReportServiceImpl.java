package com.interfaces.iat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.testreport.TestReportCreateInputDto;
import com.interfaces.iat.dto.input.testreport.TestReportUpdateInputDto;
import com.interfaces.iat.dto.output.testreport.TestReportOutputDto;
import com.interfaces.iat.entity.ProjectEntity;
import com.interfaces.iat.entity.TestReportEntity;
import com.interfaces.iat.entity.TestRecordEntity;
import com.interfaces.iat.mapper.TestReportMapper;
import com.interfaces.iat.service.ProjectService;
import com.interfaces.iat.service.TestRecordService;
import com.interfaces.iat.service.TestReportService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class TestReportServiceImpl extends ServiceImpl<TestReportMapper, TestReportEntity> implements TestReportService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    TestRecordService testRecordService;
    @Autowired
    ProjectService projectService;

    @Override
    public ResponseData<List<TestReportOutputDto>> query(Integer pageIndex, Integer pageSize, Integer testRecordId, Integer projectId) {
        ResponseData<List<TestReportOutputDto>> responseData;

        try {
            QueryWrapper<TestReportEntity> queryWrapper = new QueryWrapper<>();
            if(testRecordId != null) {
                queryWrapper.eq("test_record_id", testRecordId);
            }
            if(projectId != null) {
                queryWrapper.eq("project_id", projectId);
            }
            queryWrapper.eq("is_delete",false); //只取没有删除的
            queryWrapper.orderByDesc("id");
            IPage<TestReportEntity> queryPage = new Page<>(pageIndex, pageSize);
            queryPage = this.page(queryPage,queryWrapper);

            responseData = ResponseData.success(queryPage.getRecords().stream().map(s->modelMapper.map(s, TestReportOutputDto.class)));
            responseData.setTotal(queryPage.getTotal());
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<List<TestReportOutputDto>> queryByProjectId(Integer projectId) {
        ResponseData<List<TestReportOutputDto>> responseData;

        try {
            QueryWrapper<TestReportEntity> queryWrapper = new QueryWrapper<>();
            if(projectId != null) {
                queryWrapper.eq("project_id", projectId);
            }
            queryWrapper.eq("is_delete",false); //只取没有删除的
            queryWrapper.orderByDesc("id");
            List <TestReportEntity> entities = this.list(queryWrapper);
            List <TestReportOutputDto> outputDtos = entities.stream().map(s -> modelMapper.map(s, TestReportOutputDto.class)).collect(Collectors.toList());

            responseData = ResponseData.success(outputDtos);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData <TestReportOutputDto> getById(Integer id) {
        ResponseData<TestReportOutputDto> responseData;
        try {
            TestReportEntity entity = super.getById(id);

            TestReportOutputDto outputDto = modelMapper.map(entity,TestReportOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<TestReportOutputDto> create(TestReportCreateInputDto inputDto) {
        ResponseData<TestReportOutputDto> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //所属测试记录是否存在
            QueryWrapper<TestRecordEntity> testRecordEntityQueryWrapper = new QueryWrapper<>();
            testRecordEntityQueryWrapper.eq("id", inputDto.getTestRecordId());
            testRecordEntityQueryWrapper.eq("is_delete", false);
            Integer existCount = testRecordService.count(testRecordEntityQueryWrapper);
            if(existCount<=0){
                checkMsgs.add("所属运行记录不存在");
            }
            //所属项目是否存在
            QueryWrapper<ProjectEntity> projectQueryWrapper = new QueryWrapper<>();
            projectQueryWrapper.eq("id", inputDto.getProjectId());
            projectQueryWrapper.eq("is_delete", false);
            existCount = projectService.count(projectQueryWrapper);
            if(existCount<=0){
                checkMsgs.add("所属项目不存在");
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            TestReportEntity entity = modelMapper.map(inputDto,TestReportEntity.class);
            entity.setIsDelete(false);
            this.save(entity);
            TestReportOutputDto outputDto = modelMapper.map(entity,TestReportOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<TestReportOutputDto> update(TestReportUpdateInputDto inputDto) {
        ResponseData<TestReportOutputDto> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //所属测试记录是否存在
            QueryWrapper<TestRecordEntity> testRecordEntityQueryWrapper = new QueryWrapper<>();
            testRecordEntityQueryWrapper.eq("id", inputDto.getTestRecordId());
            testRecordEntityQueryWrapper.eq("is_delete", false);
            Integer existCount = testRecordService.count(testRecordEntityQueryWrapper);
            if(existCount<=0){
                checkMsgs.add("所属运行记录不存在");
            }
            //所属项目是否存在
            QueryWrapper<ProjectEntity> projectQueryWrapper = new QueryWrapper<>();
            projectQueryWrapper.eq("id", inputDto.getProjectId());
            projectQueryWrapper.eq("is_delete", false);
            existCount = projectService.count(projectQueryWrapper);
            if(existCount<=0){
                checkMsgs.add("所属项目不存在");
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            TestReportEntity entity = modelMapper.map(inputDto,TestReportEntity.class);
            entity.setIsDelete(false);
            this.save(entity);
            TestReportOutputDto outputDto = modelMapper.map(entity,TestReportOutputDto.class);

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
            QueryWrapper<TestReportEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",id);
            queryWrapper.eq("is_delete",false);
            TestReportEntity moduleEntity = this.getOne(queryWrapper,false);
            if (moduleEntity==null) {
                checkMsgs.add("测试报告不存在");
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
