package com.interfaces.iat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interfaces.iat.entity.UserEntity;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.account.LoginInputDto;
import com.interfaces.iat.dto.input.account.RegisterDto;
import com.interfaces.iat.dto.output.account.LoginOutputDto;
import org.springframework.cache.annotation.CacheConfig;

/**
 * 账号服务接口
 */
public interface AccountService extends IService<UserEntity> {
    /**
     * 登录
     * @param loginDto
     * @return
     */
    ResponseData<LoginOutputDto> login(LoginInputDto loginDto);

    /**
     * 注册
     * @param registerDto
     * @return
     */
    ResponseData<Boolean> register(RegisterDto registerDto);
}
