package com.interfaces.iat.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.job.JobCreateInputDto;
import com.interfaces.iat.dto.input.job.JobUpdateInputDto;
import com.interfaces.iat.dto.input.job.XxlJobInputDto;
import com.interfaces.iat.dto.output.job.JobOutputDto;
import com.interfaces.iat.entity.JobEntity;
import com.interfaces.iat.entity.enumeration.JobStatus;
import com.interfaces.iat.mapper.JobMapper;
import com.interfaces.iat.service.JobService;
import com.interfaces.iat.util.XxlJobUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, JobEntity> implements JobService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    XxlJobUtil xxlJobUtil;

    //每个测试任务可以的定时执行数量
    @Value("${per.task.job.count:5}")
    int perTaskJobCount;

    /**
     * XXL-Job回调业务地址
     */
    private static final String XXL_JOB_CALLBACK_URL="/xxljobcallback/task/run?taskId=%d&environmentId=%d&projectId=%d";

    @Override
    public ResponseData<JobOutputDto> getById(Integer id) {
        ResponseData<JobOutputDto> responseData;
        try {
            JobEntity entity = super.getById(id);

            JobOutputDto outputDto = modelMapper.map(entity,JobOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<JobOutputDto> create(JobCreateInputDto inputDto) {
        ResponseData<JobOutputDto> responseData;

        try {
            List<String> checkMsgs = new ArrayList<>();
            //测试任务下的定时执行是否超过限制
            QueryWrapper<JobEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("task_id",inputDto.getTaskId());
            queryWrapper.eq("is_delete",false);
            List<JobEntity> jobEntities = this.list(queryWrapper);
            if (jobEntities.size()>=perTaskJobCount) {
                checkMsgs.add(String.format("测试任务下的定时执行数量已经达到%d个，不能继续创建",perTaskJobCount));
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            JobEntity entity = modelMapper.map(inputDto,JobEntity.class);
            entity.setStatus(JobStatus.Draft.ordinal());
            entity.setIsDelete(false);
            this.save(entity);
            JobOutputDto outputDto = modelMapper.map(entity,JobOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData <JobOutputDto> update(JobUpdateInputDto inputDto) {
        ResponseData<JobOutputDto> responseData;

        try {
            JobEntity entity = modelMapper.map(inputDto,JobEntity.class);
            entity.setIsDelete(false);
            this.updateById(entity);
            JobOutputDto outputDto = modelMapper.map(entity,JobOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData <JobOutputDto> start(Integer id) {
        ResponseData<JobOutputDto> responseData;
        try {
            JobEntity entity = super.getById(id);

            //添加定时任务
            XxlJobInputDto xxlJobInputDto = new XxlJobInputDto();
            xxlJobInputDto.setName(entity.getName());
            xxlJobInputDto.setCron(entity.getCron());
            xxlJobInputDto.setCallbackUrl(String.format(XXL_JOB_CALLBACK_URL,entity.getTaskId(),entity.getEnvironmentId(),entity.getProjectId()));

            Map<String,Object> result = xxlJobUtil.addJob(xxlJobInputDto);
            Integer code = (Integer)result.get("code");
            if(code != 200) {
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage("启动失败，返回内容为["+ JSON.toJSONString(result));

                return responseData;
            }
            Integer xxlJobId =Integer.parseInt(result.get("content").toString());

            entity.setXxlJobId(xxlJobId);
            entity.setStatus(JobStatus.Started.ordinal());
            this.updateById(entity);

            JobOutputDto outputDto = modelMapper.map(entity,JobOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData <JobOutputDto> stop(Integer id) {
        ResponseData<JobOutputDto> responseData;
        try {
            JobEntity entity = super.getById(id);

            Map<String,Object> result = xxlJobUtil.stopJob(entity.getXxlJobId());
            Integer code = (Integer)result.get("code");
            if(code != 200) {
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage("停止失败，返回内容为["+ JSON.toJSONString(result));

                return responseData;
            }

            entity.setStatus(JobStatus.Stoped.ordinal());
            this.updateById(entity);

            JobOutputDto outputDto = modelMapper.map(entity,JobOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData <JobOutputDto> delete(Integer id) {
        ResponseData<JobOutputDto> responseData;
        try {
            JobEntity entity = super.getById(id);

            entity.setIsDelete(true);
            this.updateById(entity);

            JobOutputDto outputDto = modelMapper.map(entity,JobOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }
}
