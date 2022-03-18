package com.interfaces.iat.controller;

import com.interfaces.iat.annotation.UserRight;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.output.testreport.TestReportOutputDto;
import com.interfaces.iat.service.TestReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(value="测试报告控制器",tags={"测试报告控制器"})
@RestController
@RequestMapping("/testreport")
@UserRight(roles = {"admin","staff"})
public class TestReportController {
    @Autowired
    TestReportService testReportService;

    @ApiOperation(value="根据ID获取", notes="根据ID获取")
    @GetMapping("/{id}")
    public ResponseData<TestReportOutputDto> getById(@PathVariable Integer id){
        return testReportService.getById(id);
    }
}
