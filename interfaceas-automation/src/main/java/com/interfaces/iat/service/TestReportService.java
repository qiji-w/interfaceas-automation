package com.interfaces.iat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interfaces.iat.dto.input.testreport.TestReportCreateInputDto;
import com.interfaces.iat.dto.input.testreport.TestReportUpdateInputDto;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.output.testreport.TestReportOutputDto;
import com.interfaces.iat.entity.TestReportEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 测试报告服务接口
 */
public interface TestReportService extends IService<TestReportEntity> {
    /**
     * 分页查询列表
     * @param pageIndex
     * @param pageSize
     * * @param moduleId
     * @param projectId
     * @return
     */
    ResponseData<List<TestReportOutputDto>> query(Integer pageIndex, Integer pageSize, Integer testRecordId, Integer projectId);

    /**
     * 查询所有
     * @return
     */
    ResponseData<List<TestReportOutputDto>> queryByProjectId(Integer projectId);

    /**
     * 根据id获取
     * @param id
     * @return
     */
    ResponseData<TestReportOutputDto> getById(Integer id);

    /**
     * 创建
     * @param inputDto
     * @return
     */
    ResponseData<TestReportOutputDto> create(TestReportCreateInputDto inputDto);

    /**
     * 更新
     * @param inputDto
     * @return
     */
    ResponseData<TestReportOutputDto> update(TestReportUpdateInputDto inputDto);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    ResponseData<Boolean> delete(Integer id);
}
