package com.interfaces.iat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.project.ProjectCreateInputDto;
import com.interfaces.iat.dto.input.project.ProjectUpdateInputDto;
import com.interfaces.iat.dto.output.project.ProjectOutputDto;
import com.interfaces.iat.entity.ProjectEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 项目服务接口
 */
public interface ProjectService extends IService<ProjectEntity> {
    /**
     * 查询所有
     * @return
     */
    ResponseData<List<ProjectOutputDto>> queryAll();

    /**
     * 根据id获取项目
     * @param id
     * @return
     */
    ResponseData<ProjectOutputDto> getById(Integer id);

    /**
     * 创建项目
     * @param projectCreateInputDto
     * @return
     */
    ResponseData<ProjectOutputDto> create(ProjectCreateInputDto projectCreateInputDto);

    /**
     * 更新项目
     * @param projectUpdateInputDto
     * @return
     */
    ResponseData<ProjectOutputDto> update(ProjectUpdateInputDto projectUpdateInputDto);

    /**
     * 根据id删除项目
     * @param id
     * @return
     */
    ResponseData<Boolean> delete(Integer id);
}
