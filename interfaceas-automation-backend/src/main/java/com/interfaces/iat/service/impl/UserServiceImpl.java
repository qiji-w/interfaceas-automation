package com.interfaces.iat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interfaces.iat.dto.input.user.UserCreateInputDto;
import com.interfaces.iat.dto.input.user.UserResetPasswordInputDto;
import com.interfaces.iat.entity.UserEntity;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.user.UserUpdateInputDto;
import com.interfaces.iat.dto.output.user.UserOutputDto;
import com.interfaces.iat.mapper.UserMapper;
import com.interfaces.iat.service.UserService;
import com.interfaces.iat.util.PasswordUtil;
import com.interfaces.iat.util.SessionUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    SessionUtil sessionUtil;

    private final String[] ROLES = {"admin","staff"};
    @Override
    public ResponseData<List<UserOutputDto>> query(Integer pageIndex, Integer pageSize, String username,String name) {
        ResponseData<List<UserOutputDto>> responseData = new ResponseData<>();

        try {
            QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
            if(username != null) {
                queryWrapper.like("username", username);
            }
            if(name != null) {
                queryWrapper.like("name", name);
            }
            queryWrapper.eq("is_delete",false);
            queryWrapper.orderByDesc("id");
            IPage<UserEntity> queryPage = new Page<>(pageIndex, pageSize);
            queryPage = this.page(queryPage,queryWrapper);

            List<UserOutputDto> outputDtos =  queryPage.getRecords().stream().map(s->modelMapper.map(s,UserOutputDto.class)).collect(Collectors.toList());

            responseData.setData(outputDtos);
            responseData.setTotal(queryPage.getTotal());
            responseData.setCode(ResponseData.SUCCESS_CODE);
            responseData.setMessage("查询成功。");
        } catch (Exception ex) {
            ex.printStackTrace();
            responseData = ResponseData.failure(ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData <List <UserOutputDto>> queryAll() {
        ResponseData<List<UserOutputDto>> responseData;

        try {
            List <UserEntity> entities = this.list();
            List <UserOutputDto> outputDtos = entities.stream().filter(s->s.getIsDelete()==false).sorted(Comparator.comparing(UserEntity::getName).reversed()) .map(s -> modelMapper.map(s, UserOutputDto.class)).collect(Collectors.toList());

            responseData = ResponseData.success(outputDtos);
        }catch (Exception ex){
            ex.printStackTrace();
            responseData = ResponseData.failure(ex.toString());
        }
        return responseData;
    }

    @Override
    public ResponseData <UserOutputDto> getById(Integer id) {
        ResponseData<UserOutputDto> responseData;
        try {
            UserEntity entity = super.getById(id);

            UserOutputDto outputDto = modelMapper.map(entity,UserOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            ex.printStackTrace();
            responseData = ResponseData.failure(ex.toString());
        }
        return responseData;
    }

    @Override
    public ResponseData <UserOutputDto> create(UserCreateInputDto inputDto) {
        ResponseData<UserOutputDto> responseData;
        try {
            List<String> checkMsgs = new ArrayList<>();
            //验证用户名是否存在
            QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username",inputDto.getUsername().trim());
            queryWrapper.eq("is_delete",false);
            UserEntity userEntity = this.getOne(queryWrapper,false);
            if(userEntity!=null){
                checkMsgs.add(String.format("用户名[%s]已存在",String.join(",",inputDto.getUsername())));
            }
            //验证角色是否存在
            List<String> nonExistRoles = inputDto.getRoles().stream().filter(s->Arrays.asList(ROLES).stream().filter(t->t.equalsIgnoreCase(s)).count()<=0).collect(Collectors.toList());
            if(nonExistRoles!=null && nonExistRoles.size()>0){
                checkMsgs.add(String.format("所属角色[%s]不存在",String.join(",",nonExistRoles)));
            }
            if(checkMsgs.size()>0){
                String checkMsg = checkMsgs.stream().collect(Collectors.joining(","));
                responseData = new ResponseData<>();
                responseData.setCode(1);
                responseData.setMessage(checkMsg);

                return responseData;
            }

            UserEntity entity = modelMapper.map(inputDto,UserEntity.class);

            String password = PasswordUtil.encrypt(inputDto.getPassword(),inputDto.getUsername());
            entity.setPassword(password);

            entity.setIsDelete(false);
            this.save(entity);

            responseData = this.getById(entity.getId());
        }catch (Exception ex){
            ex.printStackTrace();
            responseData = ResponseData.failure(ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData <UserOutputDto> update(UserUpdateInputDto inputDto) {
        ResponseData<UserOutputDto> responseData;
        try {
            List<String> checkMsgs = new ArrayList<>();
            //验证ID是否已经存在
            UserEntity existsEntity = super.getById(inputDto.getId());
            if(existsEntity == null){
                checkMsgs.add(0,String.format("用户ID[%d]不存在",inputDto.getId()));
            }else {
                //验证用户名是否存在
                QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.ne("id", inputDto.getId());
                queryWrapper.eq("username", inputDto.getUsername().trim());
                queryWrapper.eq("is_delete",false);
                UserEntity userEntity = this.getOne(queryWrapper, false);
                if (userEntity != null) {
                    checkMsgs.add(String.format("用户名[%s]已存在", String.join(",", inputDto.getUsername())));
                }
                //验证角色是否存在
                List<String> nonExistRoles = inputDto.getRoles().stream().filter(s->Arrays.asList(ROLES).stream().filter(t->t.equalsIgnoreCase(s)).count()<=0).collect(Collectors.toList());
                if(nonExistRoles!=null && nonExistRoles.size()>0){
                    checkMsgs.add(String.format("所属角色[%s]不存在",String.join(",",nonExistRoles)));
                }
            }
            if(checkMsgs.size()>0){
                String checkMsg = checkMsgs.stream().collect(Collectors.joining(","));
                responseData = new ResponseData<>();
                responseData.setCode(1);
                responseData.setMessage(checkMsg);

                return responseData;
            }

            UserEntity entity = modelMapper.map(inputDto,UserEntity.class);
            entity.setPassword(existsEntity.getPassword());
            boolean success = this.updateById(entity);

            responseData = this.getById(entity.getId());
        }catch (Exception ex){
            ex.printStackTrace();
            responseData = ResponseData.failure(ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData <Boolean> delete(Integer id) {
        ResponseData<Boolean> responseData;
        try {
            List<String> checkMsgs = new ArrayList<>();
            //验证ID是否已经存在
            UserEntity userEntity = super.getById(id);
            if(userEntity == null){
                checkMsgs.add(0,String.format("用户ID[%d]不存在",id));
            }
            if(checkMsgs.size()>0){
                String checkMsg = checkMsgs.stream().collect(Collectors.joining(","));
                responseData = new ResponseData<>();
                responseData.setCode(1);
                responseData.setMessage(checkMsg);

                return responseData;
            }

            userEntity.setIsDelete(true);

           Boolean result =   this.updateById(userEntity);

            responseData = ResponseData.success(result);
        }catch (Exception ex){
            ex.printStackTrace();
            responseData = ResponseData.failure(ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<Boolean> resetPassord(UserResetPasswordInputDto inputDto) {
        ResponseData<Boolean> responseData;
        try {
            List<String> checkMsgs = new ArrayList<>();
            UserEntity userEntity = this.getById((Serializable)inputDto.getId());

            if(userEntity == null){
                checkMsgs.add(0,String.format("用户ID[%d]不存在",inputDto.getId()));
            }
            if(checkMsgs.size()>0){
                String checkMsg = checkMsgs.stream().collect(Collectors.joining(","));
                responseData = new ResponseData<>();
                responseData.setCode(1);
                responseData.setMessage(checkMsg);

                return responseData;
            }

            String password = PasswordUtil.encrypt(inputDto.getPassword(),userEntity.getUsername());
            userEntity.setPassword(password);

            boolean success = this.updateById(userEntity);

            responseData = ResponseData.success(success);
        }catch (Exception ex){
            ex.printStackTrace();
            responseData = ResponseData.failure(ex.toString());
        }

        return responseData;
    }
}
