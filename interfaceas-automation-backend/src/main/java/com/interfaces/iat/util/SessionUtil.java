package com.interfaces.iat.util;

import com.interfaces.iat.entity.UserEntity;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Component
public class SessionUtil {
    @Autowired
    HttpSession httpSession;

    final String CURRENT_USER_KEY="currentUser";

    /**
     * 设置当前用户
     * @param currentUser
     */
    public void setCurrentUser(CurrentUser currentUser){
        httpSession.setAttribute(CURRENT_USER_KEY,currentUser);
    }

    /**
     * 获取当前用户
     * @return
     */
    public CurrentUser getCurrentUser(){
        CurrentUser currentUser = null;
         Object result = httpSession.getAttribute(CURRENT_USER_KEY);
         if(result!=null){
             currentUser = (CurrentUser)result;
         }
        return currentUser ;
    }

    @Data
    public static class CurrentUser implements Serializable {
        private UserEntity userEntity;
    }
}
