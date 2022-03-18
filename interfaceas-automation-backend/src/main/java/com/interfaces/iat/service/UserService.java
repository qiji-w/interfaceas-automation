package com.interfaces.iat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interfaces.iat.dto.input.user.UserCreateInputDto;
import com.interfaces.iat.dto.input.user.UserResetPasswordInputDto;
import com.interfaces.iat.entity.UserEntity;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.user.UserUpdateInputDto;
import com.interfaces.iat.dto.output.user.UserOutputDto;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 用户服务接口
 */
@CacheConfig(cacheNames = "TestSuitService")
public interface UserService extends IService<UserEntity> {
    /**
     * 分页查询
     * @param pageIndex
     * @param pageSize
     * @param username
     * @param name
     * @return
     */
    @Cacheable(key = "#root.methodName",unless = "#result==null")
    ResponseData<List<UserOutputDto>> query(Integer pageIndex, Integer pageSize, String username,String name);

    /**
     * 查询所有数据
     * @return
     */
    @Cacheable(key = "#root.methodName",unless = "#result==null")
    ResponseData<List<UserOutputDto>> queryAll();

    /**
     * 根据Id获取
     * @param id
     * @return
     */
    @Cacheable(key = "#root.methodName",unless = "#result==null")
    ResponseData<UserOutputDto> getById(Integer id);

    /**
     * 创建
     * @param inputDto
     * @return
     */
    @CacheEvict(allEntries = true)
    ResponseData<UserOutputDto> create(UserCreateInputDto inputDto);

    /**
     * 更新
     * @param inputDto
     * @return
     */
    @CacheEvict(allEntries = true)
    ResponseData<UserOutputDto> update(UserUpdateInputDto inputDto);

    /**
     * 删除
     * @param id
     */
    @CacheEvict(allEntries = true)
    ResponseData<Boolean> delete(Integer id);

    /**
     * 重置密码
     * @param inputDto
     * @return
     */
    @CacheEvict(allEntries = true)
    ResponseData<Boolean> resetPassord(UserResetPasswordInputDto inputDto);
}
