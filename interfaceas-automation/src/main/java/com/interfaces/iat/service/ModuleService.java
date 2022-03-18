package com.interfaces.iat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interfaces.iat.entity.ModuleEntity;
import com.interfaces.iat.dto.input.module.ModuleCreateInputDto;
import com.interfaces.iat.dto.input.module.ModuleUpdateInputDto;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.output.module.ModuleOutputDto;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 项目模块服务接口
 */
public interface ModuleService extends IService<ModuleEntity> {
    /**
     * 分页查询列表
     * @param pageIndex
     * @param pageSize
     * @param projectId
     * @return
     */
    ResponseData<List<ModuleOutputDto>> query(Integer pageIndex, Integer pageSize, Integer projectId);

    /**
     * 查询全部
     * @return
     */
    ResponseData<List<ModuleOutputDto>> queryByProjectId(Integer projectId);

    /**
     * 根据id获取
     * @param id
     * @return
     */
    ResponseData<ModuleOutputDto> getById(Integer id);

    /**
     * 创建
     * @param inputDto
     * @return
     */
    ResponseData<ModuleOutputDto> create(ModuleCreateInputDto inputDto);

    /**
     * 更新
     * @param inputDto
     * @return
     */
    ResponseData<ModuleOutputDto> update(ModuleUpdateInputDto inputDto);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    ResponseData<Boolean> delete(Integer id);
}
