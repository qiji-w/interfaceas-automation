package com.interfaces.iat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interfaces.iat.dto.input.testrecord.TestRecordCreateInputDto;
import com.interfaces.iat.dto.input.testrecord.TestRecordUpdateInputDto;
import com.interfaces.iat.entity.TestRecordEntity;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.output.testrecord.TestRecordOutputDto;
import com.interfaces.iat.dto.output.testrecord.TestRecordQueryOutputDto;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 测试记录服务接口
 */
public interface TestRecordService extends IService<TestRecordEntity> {
    /**
     * 分页查询列表
     * @param pageIndex
     * @param pageSize
     * * @param moduleId
     * @param projectId
     * @return
     */
    ResponseData<List<TestRecordQueryOutputDto>> query(Integer pageIndex, Integer pageSize, Integer enviromentId, Integer taskId, Integer projectId);

    /**
     * 查询所有
     * @return
     */
    ResponseData<List<TestRecordQueryOutputDto>> queryByProjectId(Integer taskId,Integer projectId);

    /**
     * 根据id获取
     * @param id
     * @return
     */
    ResponseData<TestRecordOutputDto> getById(Integer id);

    /**
     * 创建
     * @param inputDto
     * @return
     */
    ResponseData<TestRecordOutputDto> create(TestRecordCreateInputDto inputDto);

    /**
     * 更新
     * @param inputDto
     * @return
     */
    ResponseData<TestRecordOutputDto> update(TestRecordUpdateInputDto inputDto);
}
