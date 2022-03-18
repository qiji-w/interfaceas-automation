package com.interfaces.iat.controller;

import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.account.LoginInputDto;
import com.interfaces.iat.dto.input.account.RegisterDto;
import com.interfaces.iat.dto.output.account.LoginOutputDto;
import com.interfaces.iat.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = {"账号控制器"})
@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    AccountService accountService;

    @ApiOperation(value = "用户登录", notes = "用户登录")
    @PostMapping("/login")
    public ResponseData<LoginOutputDto> login(@RequestBody  @Validated LoginInputDto loginDto) {
        return accountService.login(loginDto);
    }

    @ApiOperation(value = "用户注册", notes = "用户注册")
    @PostMapping("/register")
    public ResponseData<Boolean> register(@RequestBody  @Validated RegisterDto registerDto){
        return accountService.register(registerDto);
    }
}
