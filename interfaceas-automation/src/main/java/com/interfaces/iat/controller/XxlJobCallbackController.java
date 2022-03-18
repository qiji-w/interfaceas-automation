package com.interfaces.iat.controller;

import com.interfaces.iat.entity.UserEntity;
import com.interfaces.iat.util.SessionUtil;
import com.interfaces.iat.dto.input.task.TaskRunInputDto;
import com.interfaces.iat.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Api(value="XXL-Job定时执行回调控制器",tags={"XXL-Job定时执行回调控制器"})
@RestController
@RequestMapping("/xxljobcallback")
public class XxlJobCallbackController {
    @Autowired
    TaskService taskService;
    @Autowired
    SessionUtil sessionUtil;

    @ApiOperation(value="运行测试任务", notes="运行测试任务")
    @GetMapping("/task/run")
    public void taskRun(@RequestParam Integer taskId,@RequestParam Integer environmentId,@RequestParam Integer projectId){
        this.generateCurrentUser();

        TaskRunInputDto taskRunInputDto = new TaskRunInputDto();
        taskRunInputDto.setTaskId(taskId);
        taskRunInputDto.setEnvironmentId(environmentId);
        taskRunInputDto.setProjectId(projectId);

        taskService.run(taskRunInputDto);
    }

    /**
     * 构建当前用户，因为是从定时执行触发，没有登录信息
     * @return
     */
    public void generateCurrentUser(){
        SessionUtil.CurrentUser currentUser  =  sessionUtil.getCurrentUser();
        if(currentUser == null){
            currentUser = new SessionUtil.CurrentUser();

            UserEntity userEntity = new UserEntity();
            userEntity.setId(-2);
            userEntity.setUsername("job");
            userEntity.setName("定时执行");
            currentUser.setUserEntity(userEntity);
        }

        sessionUtil.setCurrentUser(currentUser);
    }
}