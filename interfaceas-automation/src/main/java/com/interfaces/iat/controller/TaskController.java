package com.interfaces.iat.controller;

import com.interfaces.iat.annotation.UserRight;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.task.TaskCreateInputDto;
import com.interfaces.iat.dto.input.task.TaskRunInputDto;
import com.interfaces.iat.dto.input.task.TaskUpdateInputDto;
import com.interfaces.iat.dto.output.module.ModuleOutputDto;
import com.interfaces.iat.dto.output.task.TaskDetailOutputDto;
import com.interfaces.iat.dto.output.task.TaskOutputDto;
import com.interfaces.iat.dto.output.task.TaskQueryOutputDto;
import com.interfaces.iat.service.TaskService;
import com.interfaces.iat.dto.input.task.ChangeIsJobOrNotInputDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value="任务控制器",tags={"任务控制器"})
@RestController
@RequestMapping("/task")
@UserRight(roles = {"admin","staff"})
public class TaskController {
    @Autowired
    TaskService taskService;

    @ApiOperation(value="任务列表", notes="任务列表")
    @GetMapping("/")
    public ResponseData<List<TaskQueryOutputDto>> query(@RequestParam(defaultValue = "1") Integer pageIndex, @RequestParam(defaultValue = "10") Integer pageSize, String name, @RequestParam Integer projectId){
        return taskService.query(pageIndex,pageSize,name,projectId);
    }

    @ApiOperation(value="查询全部", notes="查询全部")
    @GetMapping("/queryByProjectId")
    public ResponseData<List<TaskOutputDto>> queryByProjectId(@RequestParam Integer projectId){
        return taskService.queryByProjectId(projectId);
    }

    @ApiOperation(value="查询全部，包含详情", notes="查询全部，包含任务->测试套件->测试用例整个树信息")
    @GetMapping("/queryDetailByProjectId")
    public ResponseData<List<TaskDetailOutputDto>> queryDetailByProjectId(@RequestParam Integer projectId){
        return taskService.queryDetailByProjectId(projectId);
    }

    @ApiOperation(value="根据ID获取", notes="根据ID获取")
    @GetMapping("/{id}")
    public ResponseData<TaskOutputDto> getById(@PathVariable Integer id){
        return taskService.getById(id);
    }

    @ApiOperation(value="创建", notes="创建")
    @PostMapping("/")
    public ResponseData<TaskOutputDto> create(@RequestBody @Validated TaskCreateInputDto inputDto){
        return taskService.create(inputDto);
    }

    @ApiOperation(value="修改", notes="修改")
    @PutMapping("/")
    public ResponseData<TaskOutputDto> update(@RequestBody @Validated TaskUpdateInputDto inputDto){
        return taskService.update(inputDto);
    }

    @ApiOperation(value="根据ID删除", notes="根据ID删除")
    @DeleteMapping("/{id}")
    public ResponseData<Boolean> delete(@PathVariable Integer id){
        return taskService.delete(id);
    }

    @ApiOperation(value="根据任务ID获取关联模块", notes="根据任务ID获取关联模块")
    @GetMapping("/getModulesByTaskId")
    public ResponseData<List<ModuleOutputDto>> getModulesByTaskId(@RequestParam Integer taskId){
        return taskService.getModulesByTaskId(taskId);
    }

    @ApiOperation(value="运行任务", notes="运行任务")
    @PostMapping("/run")
    public ResponseData<Boolean> run(@RequestBody @Validated TaskRunInputDto taskRunInputDto){
        return taskService.run(taskRunInputDto);
    }

    @ApiOperation(value="根据ID归档", notes="根据ID归档")
    @GetMapping("/archive")
    public ResponseData<Boolean> archive(@RequestParam Integer id){
        return taskService.archive(id);
    }

    @ApiOperation(value="根据ID变更任务是定时执行", notes="根据ID变更任务是定时执行")
    @PostMapping("/changeIsJobOrNot")
    public ResponseData<Boolean> changeIsJobOrNot(@RequestBody ChangeIsJobOrNotInputDto inputDto){
        return taskService.changeIsJobOrNot(inputDto);
    }
}
