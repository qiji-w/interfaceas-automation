package com.interfaces.iat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.account.LoginInputDto;
import com.interfaces.iat.dto.input.account.RegisterDto;
import com.interfaces.iat.dto.output.account.LoginOutputDto;
import com.interfaces.iat.entity.UserEntity;
import com.interfaces.iat.mapper.UserMapper;
import com.interfaces.iat.service.AccountService;
import com.interfaces.iat.util.PasswordUtil;
import com.interfaces.iat.util.SessionUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AccountServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements AccountService {
    @Autowired
    SessionUtil sessionUtil;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public ResponseData<LoginOutputDto> login(LoginInputDto inputDto) {
        ResponseData<LoginOutputDto> responseData;

        try{
            QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", inputDto.getUsername().trim());
            queryWrapper.eq("is_delete",false);
            UserEntity userEntity = this.getOne(queryWrapper,false);
            if(userEntity !=null){
                if(userEntity.getPassword().equalsIgnoreCase(PasswordUtil.encrypt(inputDto.getPassword(),inputDto.getUsername().trim())) == false){

                    responseData = new ResponseData<>();
                    responseData.setCode(3);
                    responseData.setMessage("密码不正确");

                    return responseData;
                }
            }else{
                responseData = new ResponseData<>();
                responseData.setCode(2);
                responseData.setMessage("用户名不存在");

                return responseData;
            }

            LoginOutputDto loginOutputDto = modelMapper.map(userEntity,LoginOutputDto.class);

            userEntity.setLastLoginTime(new Date());
            this.updateById(userEntity);

            SessionUtil.CurrentUser currentUser = new SessionUtil.CurrentUser();
            currentUser.setUserEntity(userEntity);
            sessionUtil.setCurrentUser(currentUser);

            responseData = ResponseData.success(loginOutputDto);
        }catch (Exception ex){
            log.error("操作异常",ex);
            responseData = ResponseData.failure(ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<Boolean> register(RegisterDto registerDto) {
        ResponseData<Boolean> responseData;

        List<String> checkMsgs = new ArrayList<>();
        if(registerDto.getUsername()==null || registerDto.getUsername().trim().isEmpty() ==true){
            checkMsgs.add("用户名不能为空");
        }
        if(registerDto.getPassword()==null || registerDto.getPassword().trim().isEmpty() ==true){
            checkMsgs.add("密码不能为空");
        }
        if(checkMsgs.size()>0){
            String checkMsg = checkMsgs.stream().collect(Collectors.joining(","));
            responseData = new ResponseData<>();
            responseData.setCode(1);
            responseData.setMessage(checkMsg);

            return responseData;
        }

        try{
            LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserEntity::getUsername, registerDto.getUsername());
            UserEntity existUser = this.getOne(queryWrapper,false);
            if(existUser ==null){
                UserEntity userEntity = modelMapper.map(registerDto,UserEntity.class);
                //注册用户都为普通用户
                List<String> roles = new ArrayList<>();
                roles.add("staff");
                userEntity.setRoles(roles);
                userEntity.setDescription("[register]");
                String password = PasswordUtil.encrypt(registerDto.getPassword(),registerDto.getUsername());
                userEntity.setPassword(password);
                Boolean result = this.save(userEntity);

                responseData = ResponseData.success(result);
            }else{
                responseData = new ResponseData<>();
                responseData.setCode(2);
                responseData.setMessage("用户名已存在");
            }
        }catch (Exception ex){
            ex.printStackTrace();
            responseData = ResponseData.failure(ex.toString());
        }
        return responseData;
    }
}
