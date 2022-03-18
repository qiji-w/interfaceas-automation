package com.interfaces.iat.controller;

import com.interfaces.iat.annotation.UserRight;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.project.ProjectCreateInputDto;
import com.interfaces.iat.dto.input.project.ProjectUpdateInputDto;
import com.interfaces.iat.dto.output.project.ProjectOutputDto;
import com.interfaces.iat.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags={"项目控制器"})
@RestController
@RequestMapping("/project")
@UserRight(roles = {"admin","staff"})
public class ProjectController {
    @Autowired
    ProjectService projectsService;

    @ApiOperation(value="项目列表", notes="项目列表")
    @GetMapping("/queryAll")
    public ResponseData<List<ProjectOutputDto>> queryAll(){
        return projectsService.queryAll();
    }

    @ApiOperation(value="根据ID获取项目", notes="根据ID获取项目")
    @GetMapping("/{id}")
    public ResponseData<ProjectOutputDto> getById(@PathVariable Integer id){
        return projectsService.getById(id);
    }

    @ApiOperation(value="创建项目", notes="创建项目")
    @PostMapping("/")
    public ResponseData<ProjectOutputDto> create(@RequestBody @Validated ProjectCreateInputDto projectCreateInputDto){
        return projectsService.create(projectCreateInputDto);
    }

    @ApiOperation(value="修改项目", notes="修改项目")
    @PutMapping("/")
    public ResponseData<ProjectOutputDto> update(@RequestBody @Validated ProjectUpdateInputDto projectUpdateInputDto){
        return projectsService.update(projectUpdateInputDto);
    }

    @ApiOperation(value="根据ID删除项目", notes="根据ID删除项目")
    @DeleteMapping("/{id}")
    public ResponseData<Boolean> delete(@PathVariable Integer id){
        return projectsService.delete(id);
    }
}
