package com.interfaces.iat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interfaces.iat.dto.input.testcase.TestCaseCreateInputDto;
import com.interfaces.iat.dto.input.testcase.TestCaseUpdateInputDto;
import com.interfaces.iat.entity.TestCaseEntity;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.output.testcase.TestCaseOutputDto;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 测试用例服务接口
 */
public interface TestCaseService extends IService<TestCaseEntity> {

    /**
     * 分页查询列表
     * @param pageIndex
     * @param pageSize
     * * @param moduleId
     * @param projectId
     * @return
     */
    ResponseData<List<TestCaseOutputDto>> query(Integer pageIndex, Integer pageSize, Integer interfaceId, Integer testSuitId, Integer taskId, Integer projectId);

    /**
     * 查询所有
     * @return
     */
    ResponseData<List<TestCaseOutputDto>> queryByProjectId(Integer projectId);

    /**
     * 根据id获取
     * @param id
     * @return
     */
    ResponseData<TestCaseOutputDto> getById(Integer id);

    /**
     * 创建
     * @param inputDto
     * @return
     */
    ResponseData<TestCaseOutputDto> create(TestCaseCreateInputDto inputDto);

    /**
     * 更新
     * @param inputDto
     * @return
     */
    ResponseData<TestCaseOutputDto> update(TestCaseUpdateInputDto inputDto);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    ResponseData<Boolean> delete(Integer id);

    /**
     * 拷贝用例
     * @param inputDto
     * @return
     */
    ResponseData<TestCaseOutputDto> copy(TestCaseCreateInputDto inputDto);
}
