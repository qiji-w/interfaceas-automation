package com.interfaces.iat.controller;

import com.interfaces.iat.annotation.UserRight;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.enviroment.EnvironmentCreateInputDto;
import com.interfaces.iat.dto.input.enviroment.EnvironmentUpdateInputDto;
import com.interfaces.iat.dto.output.enviroment.EnvironmentOutputDto;
import com.interfaces.iat.service.EnvironmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value="环境控制器",tags={"环境控制器"})
@RestController
@RequestMapping("/environment")
@UserRight(roles = {"admin","staff"})
public class EnvironmentController {
    @Autowired
    EnvironmentService environmentsService;

    @ApiOperation(value="查询全部", notes="查询全部")
    @GetMapping("/queryByProjectId")
    public ResponseData<List<EnvironmentOutputDto>> queryByProjectId(@RequestParam Integer projectId){
        return environmentsService.queryByProjectId(projectId);
    }

    @ApiOperation(value="根据ID获取", notes="根据ID获取")
    @GetMapping("/{id}")
    public ResponseData<EnvironmentOutputDto> getById(@PathVariable Integer id){
        return environmentsService.getById(id);
    }

    @ApiOperation(value="创建", notes="创建")
    @PostMapping("/")
    public ResponseData<EnvironmentOutputDto> create(@RequestBody @Validated EnvironmentCreateInputDto inputDto){
        return environmentsService.create(inputDto);
    }

    @ApiOperation(value="修改", notes="修改")
    @PutMapping("/")
    public ResponseData<EnvironmentOutputDto> update(@RequestBody @Validated EnvironmentUpdateInputDto inputDto){
        return environmentsService.update(inputDto);
    }

    @ApiOperation(value="根据ID删除", notes="根据ID删除")
    @DeleteMapping("/{id}")
    public ResponseData<Boolean> delete(@PathVariable Integer id){
        return environmentsService.delete(id);
    }
}
