package com.interfaces.iat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.testsuit.TestSuitCreateInputDto;
import com.interfaces.iat.dto.input.testsuit.TestSuitUpdateInputDto;
import com.interfaces.iat.dto.output.testsuit.TestSuitOutputDto;
import com.interfaces.iat.entity.TestSuitEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 测试套件服务接口
 */
public interface TestSuitService extends IService<TestSuitEntity> {
    /**
     * 查询所有
     * @return
     */
    ResponseData<List<TestSuitOutputDto>> queryByProjectId(Integer projectId);

    /**
     * 根据id获取
     * @param id
     * @return
     */
    ResponseData<TestSuitOutputDto> getById(Integer id);

    /**
     * 创建
     * @param inputDto
     * @return
     */
    ResponseData<TestSuitOutputDto> create(TestSuitCreateInputDto inputDto);

    /**
     * 更新
     * @param inputDto
     * @return
     */
    ResponseData<TestSuitOutputDto> update(TestSuitUpdateInputDto inputDto);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    ResponseData<Boolean> delete(Integer id);
}
