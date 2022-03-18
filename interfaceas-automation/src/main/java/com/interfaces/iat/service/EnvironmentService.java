package com.interfaces.iat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interfaces.iat.dto.input.enviroment.EnvironmentCreateInputDto;
import com.interfaces.iat.dto.input.enviroment.EnvironmentUpdateInputDto;
import com.interfaces.iat.entity.EnvironmentEntity;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.output.enviroment.EnvironmentOutputDto;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 项目环境服务接口
 */
public interface EnvironmentService extends IService<EnvironmentEntity> {
    /**
     * 查询所有
     * @return
     */
    ResponseData<List<EnvironmentOutputDto>> queryByProjectId(Integer projectId);

    /**
     * 根据id获取
     * @param id
     * @return
     */
    ResponseData<EnvironmentOutputDto> getById(Integer id);

    /**
     * 创建
     * @param inputDto
     * @return
     */
    ResponseData<EnvironmentOutputDto> create(EnvironmentCreateInputDto inputDto);

    /**
     * 更新
     * @param inputDto
     * @return
     */
    ResponseData<EnvironmentOutputDto> update(EnvironmentUpdateInputDto inputDto);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    ResponseData<Boolean> delete(Integer id);
}
