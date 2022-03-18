package com.interfaces.iat.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.interfaces.iat.entity.UserEntity;
import com.interfaces.iat.util.SessionUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class CustomMetaObjectHandler implements MetaObjectHandler {
    @Autowired
    SessionUtil sessionUtil;

    @Override
    public void insertFill(MetaObject metaObject) {
        SessionUtil.CurrentUser currentUser = this.getCurrentUser();

        this.setFieldValByName("createById", currentUser.getUserEntity().getId(), metaObject);
        this.setFieldValByName("createByName", currentUser.getUserEntity().getName(), metaObject);
        this.setFieldValByName("updateById", currentUser.getUserEntity().getId(), metaObject);
        this.setFieldValByName("updateByName", currentUser.getUserEntity().getName(), metaObject);
        Date current = new Date();
        this.setFieldValByName("createTime", current, metaObject);
        this.setFieldValByName("updateTime", current, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        SessionUtil.CurrentUser currentUser = this.getCurrentUser();

        this.setFieldValByName("updateById", currentUser.getUserEntity().getId(), metaObject);
        this.setFieldValByName("updateByName", currentUser.getUserEntity().getName(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }

    /**
     * 获取当前用户
     * @return
     */
    public SessionUtil.CurrentUser getCurrentUser(){
        SessionUtil.CurrentUser currentUser  =  sessionUtil.getCurrentUser();
        if(currentUser == null){
            currentUser = new SessionUtil.CurrentUser();

            UserEntity userEntity = new UserEntity();
            userEntity.setId(-1);
            userEntity.setUsername("anonymous");
            userEntity.setName("匿名用户");
            currentUser.setUserEntity(userEntity);
        }

        return currentUser;
    }
}
