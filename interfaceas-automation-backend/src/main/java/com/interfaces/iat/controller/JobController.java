package com.interfaces.iat.controller;

import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.job.JobCreateInputDto;
import com.interfaces.iat.dto.input.job.JobUpdateInputDto;
import com.interfaces.iat.dto.output.job.JobOutputDto;
import com.interfaces.iat.service.JobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Api(value="定时执行控制器",tags={"定时执行控制器"})
@RestController
@RequestMapping("/job")
public class JobController {
    @Autowired
    JobService jobService;

    /**
     * 根据ID获取
     * @param id
     * @return
     */
    @ApiOperation(value="根据ID获取", notes="根据ID获取")
    @GetMapping("/{id}")
    public ResponseData<JobOutputDto> getById(@PathVariable Integer id){
        return jobService.getById(id);
    }

    /**
     * 创建
     * @param inputDto
     * @return
     */
    @ApiOperation(value="创建接口", notes="创建接口")
    @PostMapping("/")
    public ResponseData<JobOutputDto> create(@RequestBody @Validated JobCreateInputDto inputDto){
        return jobService.create(inputDto);
    }

    /**
     * 修改
     * @param inputDto
     * @return
     */
    @ApiOperation(value="修改接口", notes="修改接口")
    @PutMapping("/")
    public ResponseData<JobOutputDto> update(@RequestBody  @Validated JobUpdateInputDto inputDto){
        return jobService.update(inputDto);
    }

    /**
     * 启动
     * @param id
     * @return
     */
    @ApiOperation(value="启动接口", notes="启动接口")
    @GetMapping("/start")
    public ResponseData<JobOutputDto> start(@RequestParam Integer id){
        return jobService.start(id);
    }

    /**
     * 停止
     * @param id
     * @return
     */
    @ApiOperation(value="停止接口", notes="停止接口")
    @GetMapping("/stop")
    public ResponseData<JobOutputDto> stop(@RequestParam Integer id){
        return jobService.stop(id);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @ApiOperation(value="删除接口", notes="删除接口")
    @DeleteMapping("/{id}")
    public ResponseData<JobOutputDto> delete(@PathVariable Integer id){
        return jobService.delete(id);
    }
}
