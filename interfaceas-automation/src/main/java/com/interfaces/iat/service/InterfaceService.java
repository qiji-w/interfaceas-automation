package com.interfaces.iat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interfaces.iat.dto.input.inf.InterfaceCreateInputDto;
import com.interfaces.iat.dto.input.inf.InterfaceUpdateInputDto;
import com.interfaces.iat.dto.output.inf.InterfaceOutputDto;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.output.inf.InterfaceQueryOutputDto;
import com.interfaces.iat.entity.InterfaceEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 项目接口服务接口
 */
public interface InterfaceService extends IService<InterfaceEntity> {
    /**
     * 分页查询列表
     * @param pageIndex
     * @param pageSize
     * * @param moduleId
     * @param projectId
     * @return
     */
    ResponseData<List<InterfaceQueryOutputDto>> query(Integer pageIndex, Integer pageSize, Integer moduleId, Integer projectId);

    /**
     * 查询所有
     * @return
     */
    ResponseData<List<InterfaceOutputDto>> queryByProjectId(Integer projectId);

    /**
     * 根据id获取
     * @param id
     * @return
     */
    ResponseData<InterfaceOutputDto> getById(Integer id);

    /**
     * 创建
     * @param inputDto
     * @return
     */
    ResponseData<InterfaceOutputDto> create(InterfaceCreateInputDto inputDto);

    /**
     * 更新
     * @param inputDto
     * @return
     */
    ResponseData<InterfaceOutputDto> update(InterfaceUpdateInputDto inputDto);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    ResponseData<Boolean> delete(Integer id);
}
