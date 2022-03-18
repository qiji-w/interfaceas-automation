package com.interfaces.iat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.job.JobCreateInputDto;
import com.interfaces.iat.dto.input.job.JobUpdateInputDto;
import com.interfaces.iat.dto.output.job.JobOutputDto;
import com.interfaces.iat.entity.JobEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * 定时任务服务接口
 */
public interface JobService extends IService<JobEntity> {
    /**
     * 根据id获取
     * @param id
     * @return
     */
    ResponseData<JobOutputDto> getById(Integer id);
    /**
     * 创建
     * @param inputDto
     * @return
     */
    ResponseData<JobOutputDto> create(JobCreateInputDto inputDto);

    /**
     * 修改
     * @param inputDto
     * @return
     */
    ResponseData<JobOutputDto> update(JobUpdateInputDto inputDto);

    /**
     * 启动
     * @param id
     * @return
     */
    ResponseData<JobOutputDto> start(Integer id);

    /**
     * 停止
     * @param id
     * @return
     */
    ResponseData<JobOutputDto> stop(Integer id);

    /**
     * 删除
     * @param id
     * @return
     */
    ResponseData<JobOutputDto> delete(Integer id);
}
