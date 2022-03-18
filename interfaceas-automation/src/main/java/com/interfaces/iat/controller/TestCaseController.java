package com.interfaces.iat.controller;

import com.alibaba.fastjson.JSONObject;
import com.interfaces.iat.annotation.UserRight;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.testcase.TestCaseCreateInputDto;
import com.interfaces.iat.dto.input.testcase.TestCaseUpdateInputDto;
import com.interfaces.iat.dto.output.testcase.TestCaseOutputDto;
import com.interfaces.iat.service.TestCaseService;
import com.interfaces.iat.util.ReplaceUtil;
import com.interfaces.iat.util.RestAssuredUtil;
import com.interfaces.iat.util.RandomUtil;
import io.restassured.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value="测试用例控制器",tags={"测试用例控制器"})
@RestController
@RequestMapping("/testcase")
@UserRight(roles = {"admin","staff"})
public class TestCaseController {
    @Autowired
    TestCaseService testCasesService;

    @ApiOperation(value="按条件分页查询", notes="按条件分页查询")
    @GetMapping("/")
    public ResponseData<List<TestCaseOutputDto>> query(@RequestParam(defaultValue = "1") Integer pageIndex, @RequestParam(defaultValue = "10") Integer pageSize,
                                                       @RequestParam Integer interfaceId, @RequestParam Integer testSuitId, Integer taskId, Integer projectId){
        return testCasesService.query(pageIndex,pageSize,interfaceId,testSuitId,taskId,projectId);
    }

    @ApiOperation(value="查询全部", notes="查询全部")
    @GetMapping("/queryByProjectId")
    public ResponseData<List<TestCaseOutputDto>> queryByProjectId(@RequestParam Integer projectId){
        return testCasesService.queryByProjectId(projectId);
    }

    @ApiOperation(value="根据ID获取", notes="根据ID获取")
    @GetMapping("/{id}")
    public ResponseData<TestCaseOutputDto> getById(@PathVariable Integer id){
        return testCasesService.getById(id);
    }

    @ApiOperation(value="创建接口", notes="创建接口")
    @PostMapping("/")
    public ResponseData<TestCaseOutputDto> create(@RequestBody @Validated TestCaseCreateInputDto inputDto){
        return testCasesService.create(inputDto);
    }

    @ApiOperation(value="修改接口", notes="修改接口")
    @PutMapping("/")
    public ResponseData<TestCaseOutputDto> update(@RequestBody @Validated TestCaseUpdateInputDto inputDto){
        return testCasesService.update(inputDto);
    }

    @ApiOperation(value="根据ID删除接口", notes="根据ID删除接口")
    @DeleteMapping("/{id}")
    public ResponseData<Boolean> delete(@PathVariable Integer id){
        return testCasesService.delete(id);
    }

    @ApiOperation(value="拷贝接口", notes="拷贝接口")
    @PutMapping("/copy")
    public ResponseData<TestCaseOutputDto> copy(@RequestBody @Validated TestCaseCreateInputDto inputDto){
        return testCasesService.copy(inputDto);
    }

    @ApiOperation(value="发送一个请求", notes="发送一个请求")
    @PostMapping("/run")
    public ResponseData<Object> run(@RequestBody JSONObject requestData){
        //内容替换
        String phone = RandomUtil.randomPhone();
        requestData = JSONObject.parseObject(ReplaceUtil.replacePhone(requestData.toString(),phone));
        String userName = RandomUtil.randomUsername();
        requestData = JSONObject.parseObject(ReplaceUtil.replaceUserName(requestData.toString(),userName));

        Response response = RestAssuredUtil.request(requestData);
        JSONObject result = new JSONObject();
        result.put("status_code", "200");
        result.put("headers",response.getHeaders());
        result.put("cookies",response.getCookies());
        try {
            result.put("json", response.jsonPath().get());
        }catch (Exception ex){
            result.put("json","");
            result.put("body",response.getBody().print());
        }
        return   ResponseData.success(result);
    }
}
