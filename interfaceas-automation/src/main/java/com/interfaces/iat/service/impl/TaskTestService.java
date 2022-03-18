package com.interfaces.iat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interfaces.iat.dto.output.enviroment.EnvironmentOutputDto;
import com.interfaces.iat.dto.output.inf.InterfaceOutputDto;
import com.interfaces.iat.dto.output.testcase.TestCaseOutputDto;
import com.interfaces.iat.dto.output.testreport.*;
import com.interfaces.iat.entity.*;
import com.interfaces.iat.service.TaskService;
import com.interfaces.iat.service.TestRecordService;
import com.interfaces.iat.service.TestReportService;
import com.interfaces.iat.service.TestSuitService;

import com.interfaces.iat.util.*;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskTestService {
    @Autowired
    CacheManager cacheManager;
    @Autowired
    TestRecordService testRecordService;
    @Autowired
    TestReportService testReportService;
    @Autowired
    TestSuitService testSuitService;
    @Autowired
    TaskService taskService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;

    private ByteArrayOutputStream byteArrayOutputStream;

    private static String GLOBAL_CACHE_NAME = "task_%d";

    /**
     * 测试
     *
     * @param environmentEntity
     * @param interfaceEntities
     * @param testCaseEntities
     */
    @Transactional
    public void test(EnvironmentEntity environmentEntity, List<InterfaceEntity> interfaceEntities, List<TestCaseEntity> testCaseEntities, TestRecordEntity testRecordEntity, SessionUtil.CurrentUser currentUser) throws JsonProcessingException {
        //设置RestAssured日志输出流
        byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(printStream));

        List<TestResultCaseOutputDto> testResultCaseOutputDtos = new ArrayList<>();
        //测试用例优先级列排序
        testCaseEntities = testCaseEntities.stream().sorted(Comparator.comparing(TestCaseEntity::getOrderIndex)).collect(Collectors.toList());
        for (TestCaseEntity testCaseEntity : testCaseEntities) {
            //每个用例接口信息
            InterfaceEntity currentInterfaceEntity = interfaceEntities.stream().filter(s -> s.getId().intValue() == testCaseEntity.getInterfaceId().intValue()).findFirst().orElse(null);
            if (currentInterfaceEntity != null) {
                //对测试用例进行测试
                TestResultCaseOutputDto testResultCaseOutputDto = this.testCase(environmentEntity, currentInterfaceEntity, testCaseEntity, testRecordEntity);

                testResultCaseOutputDtos.add(testResultCaseOutputDto);
            }
        }
        //关闭输出流
        try {
            printStream.close();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //组织测试结果
        List<Integer> testSuitIds = testCaseEntities.stream().map(s -> s.getTestSuitId()).distinct().collect(Collectors.toList());
        TestResultOutputDto testResultOutputDto = new TestResultOutputDto();
        testResultOutputDto.setTaskId(testRecordEntity.getTaskId());
        TaskEntity taskEntity = taskService.getById((Serializable) testRecordEntity.getTaskId());
        if (taskEntity != null) {
            //测试任务名
            testResultOutputDto.setTaskName(taskEntity.getName());
        }
        //测试套件数
        testResultOutputDto.setTotalOfTestSuit((long) testSuitIds.size());
        //测试用例数
        testResultOutputDto.setTotalOfTestCase((long) testCaseEntities.size());
        //测试用例数
        testResultOutputDto.setTotalOfTestCaseForSuccess(testResultCaseOutputDtos.stream().filter(s -> s.getStatus().intValue() == 0).count());
        //失败用例数
        testResultOutputDto.setTotalOfTestCaseForFailure(testResultCaseOutputDtos.stream().filter(s -> s.getStatus().intValue() == 1).count());
        //异常用例数
        testResultOutputDto.setTotalOfTestCaseForError(testResultCaseOutputDtos.stream().filter(s -> s.getStatus().intValue() == 2).count());
        //运行用时
        double totalDuration = testResultCaseOutputDtos.stream().mapToDouble(s -> ((double) (s.getEndTime().getTime() - s.getStartTime().getTime()) / 1000)).sum();
        testResultOutputDto.setTotalDuration(totalDuration);
        //根据所有用例结果判断测试是否成功
        int status = 0;
        if (testResultOutputDto.getTotalOfTestCase().intValue() > 0) {
            if (testResultOutputDto.getTotalOfTestCaseForSuccess().intValue() <= 0) {
                status = 2;
            } else {
                if (testResultOutputDto.getTotalOfTestCaseForSuccess().intValue() < testResultOutputDto.getTotalOfTestCase().intValue()) {
                    status = 1;
                }
            }
        }
        testResultOutputDto.setStatus(status);
        //设置当前测试的项目环境快照
        testResultOutputDto.setEnvironment(modelMapper.map(environmentEntity, EnvironmentOutputDto.class));

        //组织测试套件结果集合
        QueryWrapper<TestSuitEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", testSuitIds);
        queryWrapper.eq("is_delete", false);
        List<TestSuitEntity> testSuitEntities = testSuitService.list(queryWrapper);
        if (testSuitEntities != null && testSuitEntities.size() > 0) {
            List<TestResultSuitOutputDto> testResultSuitOutputDtos = new ArrayList<>();
            for (TestSuitEntity testSuitEntity : testSuitEntities) {
                //组织单个测试套件
                TestResultSuitOutputDto testResultSuitOutputDto = new TestResultSuitOutputDto();
                testResultSuitOutputDto.setTestSuitId(testSuitEntity.getId());
                testResultSuitOutputDto.setTestSuitName(testSuitEntity.getName());

                //获取关联测试用例测试结果
                List<TestResultCaseOutputDto> currentTestResultCaseOutputDtos = testResultCaseOutputDtos.stream().filter(s -> s.getTestCase().getTestSuitId().intValue() == testSuitEntity.getId().intValue()).collect(Collectors.toList());
                testResultSuitOutputDto.setTestCaseResults(currentTestResultCaseOutputDtos);

                testResultSuitOutputDtos.add(testResultSuitOutputDto);
            }
            testResultOutputDto.setTestSuitResults(testResultSuitOutputDtos);
        }

        //添加测试报告
        TestReportEntity testReportEntity = new TestReportEntity();
        testReportEntity.setResult(objectMapper.writeValueAsString(testResultOutputDto));
        testReportEntity.setIsDelete(false);
        testReportEntity.setTestRecordId(testRecordEntity.getId());
        testReportEntity.setProjectId(testRecordEntity.getProjectId());
        //会话信息
        Date current = new Date();
        UserEntity userEntity = currentUser.getUserEntity();
        testReportEntity.setCreateById(userEntity.getId());
        testReportEntity.setCreateByName(userEntity.getName());
        testReportEntity.setCreateTime(current);
        testReportEntity.setUpdateById(userEntity.getId());
        testReportEntity.setUpdateByName(userEntity.getName());
        testReportEntity.setUpdateTime(current);
        testReportService.save(testReportEntity);

        //修改测试记录的状态
        testRecordEntity.setStatus(0);
        //会话信息
        testRecordEntity.setUpdateById(userEntity.getId());
        testRecordEntity.setUpdateByName(userEntity.getName());
        testRecordEntity.setUpdateTime(current);
        testRecordService.updateById(testRecordEntity);
    }

    /**
     * 测试单个测试用例
     *
     * @param environmentEntity
     * @param interfaceEntity
     * @param testCaseEntity
     * @param testRecordEntity
     * @return
     */
    private TestResultCaseOutputDto testCase(EnvironmentEntity environmentEntity, InterfaceEntity interfaceEntity, TestCaseEntity testCaseEntity,
                                             TestRecordEntity testRecordEntity) {
        TestResultCaseOutputDto testResultCaseOutputDto = new TestResultCaseOutputDto();

        //设置测试结果中的用例信息基础数据快照
        testResultCaseOutputDto.setTestCase(modelMapper.map(testCaseEntity, TestCaseOutputDto.class));
        testResultCaseOutputDto.setInf(modelMapper.map(interfaceEntity, InterfaceOutputDto.class));
        //随机内容替换
        this.randomReplace(interfaceEntity, testCaseEntity, testRecordEntity);
        //全局响应提取内容替换
        this.globalExtractReplace(interfaceEntity, testCaseEntity, testRecordEntity);

        //根据测试用例、测试环境构造请求数据
        JSONObject requestData = new JSONObject();
        requestData.put("url", environmentEntity.getHost() + interfaceEntity.getPath());
        requestData.put("method", interfaceEntity.getRequestMethod());
        requestData.put("request", testCaseEntity.getRequestData());

        JSONObject responseData = null;

        Date startTime = new Date();
        try {
            Response response = RestAssuredUtil.request(requestData);
            Date endTime = new Date();
            responseData = new JSONObject();
            responseData.put("status_code", response.getStatusCode());
            responseData.put("headers", response.getHeaders());
            responseData.put("cookies", response.getCookies());
            Object o = response.jsonPath().get();
            responseData.put("json", requestData.get("method").toString().equalsIgnoreCase("delete") == false ? o : "{}");

            testResultCaseOutputDto.setStatus(0);
            //获取响应输出流
            testResultCaseOutputDto.setResponseData(byteArrayOutputStream.toString("utf-8"));
            byteArrayOutputStream.reset();
            //设置接口调用耗时
            testResultCaseOutputDto.setStartTime(startTime);
            testResultCaseOutputDto.setEndTime(endTime);
            //响应提取
            List<TestResultCaseCommonDto> testResultCaseCommonDtos = this.extractData(testCaseEntity, responseData, testRecordEntity);
            testResultCaseOutputDto.setExtracts(testResultCaseCommonDtos);
            //对返回值做用例断言
            List<TestResultCaseCommonDto> testResultCaseAssertDtosOfCommon = this.assertResponse(testCaseEntity, responseData);
            testResultCaseOutputDto.setAsserts(testResultCaseAssertDtosOfCommon);
            //对返回值做数据库断言
            List<TestResultCaseCommonDto> testResultCaseAssertDtosOfDbCommon = this.dbAssertResponse(testCaseEntity, environmentEntity.getDbConfig());
            testResultCaseOutputDto.setDbAsserts(testResultCaseAssertDtosOfDbCommon);
            //根据数测试调用的用例断言、数据库断言设置用例执行结果
            if (testResultCaseAssertDtosOfCommon.stream().filter(s -> s.getResult() == null || s.getResult() == false).count() > 0 || testResultCaseAssertDtosOfDbCommon.stream().filter(s -> s.getResult() == null || s.getResult() == false).count() > 0) {
                testResultCaseOutputDto.setStatus(1);
            }
        } catch (Exception ex) {
            Date endTime = new Date();
            testResultCaseOutputDto.setStartTime(startTime);
            testResultCaseOutputDto.setEndTime(endTime);
            testResultCaseOutputDto.setStatus(2);
            testResultCaseOutputDto.setException(ExceptionUtils.getStackTrace(ex));
        }

        return testResultCaseOutputDto;
    }

    /**
     * 随机内容替换，包括替换测试用例的请求数据、返回值等等
     *
     * @param testCaseEntity
     */
    private void randomReplace(InterfaceEntity interfaceEntity, TestCaseEntity testCaseEntity, TestRecordEntity testRecordEntity) {
        Map<String, Object> cacheValue = new HashMap<>();

        //手机号
        String phone = RandomUtil.randomPhone();
        if (this.hasReplaceKey(interfaceEntity, testCaseEntity, ReplaceUtil.PHONE_KEY)) {
            this.replaceTestCase(interfaceEntity, testCaseEntity, ReplaceUtil.PHONE_KEY, phone);
            cacheValue.put(ReplaceUtil.PHONE_KEY.replaceAll("##", "#"), phone);
        }
        //用户名
        String username = RandomUtil.randomUsername();
        if (this.hasReplaceKey(interfaceEntity, testCaseEntity, ReplaceUtil.USER_NAME_KEY)) {
            this.replaceTestCase(interfaceEntity, testCaseEntity, ReplaceUtil.USER_NAME_KEY, username);
            cacheValue.put(ReplaceUtil.USER_NAME_KEY.replaceAll("##", "#"), username);
        }

        if (cacheValue.size() > 0) {
            this.addCacheItem(testRecordEntity.getTaskId(), testRecordEntity.getId(), cacheValue);
        }
    }

    /**
     * 全局响应提取替换
     *
     * @param testCaseEntity
     * @param testRecordEntity
     */
    private void globalExtractReplace(InterfaceEntity interfaceEntity, TestCaseEntity testCaseEntity, TestRecordEntity testRecordEntity) {
        String cacheName = String.format(GLOBAL_CACHE_NAME, testRecordEntity.getTaskId());
        Object result = cacheManager.getCache(cacheName).get(testRecordEntity.getId());
        Map<String, Object> cacheResult = null;
        if (result != null) {
            cacheResult = (Map<String, Object>) ((Cache.ValueWrapper) result).get();
            if (cacheResult != null && cacheResult.size() > 0) {
                for (String key : cacheResult.keySet()) {
                    this.replaceTestCase(interfaceEntity, testCaseEntity, key, cacheResult.get(key).toString());
                }
            }
        }
    }

    /**
     * 测试接口和测试用例中，是否包含指定的key，该key可以是随机Key或响应提取key
     *
     * @param interfaceEntity
     * @param testCaseEntity
     * @param key
     * @return
     */
    private boolean hasReplaceKey(InterfaceEntity interfaceEntity, TestCaseEntity testCaseEntity, String key) {
        boolean result = false;
        if (testCaseEntity.getRequestData().indexOf(key) >= 0 || testCaseEntity.getAssertion().indexOf(key) >= 0
                || testCaseEntity.getDbAssertion().indexOf(key) >= 0 || interfaceEntity.getPath().indexOf(key) >= 0) {
            result = true;
        }
        return result;
    }

    /**
     * 测试用例内容替换
     *
     * @param testCaseEntity
     * @param key
     * @param value
     */
    private void replaceTestCase(InterfaceEntity interfaceEntity, TestCaseEntity testCaseEntity, String key, String value) {
        testCaseEntity.setRequestData(ReplaceUtil.replace(testCaseEntity.getRequestData(), key, value));
        testCaseEntity.setAssertion(ReplaceUtil.replace(testCaseEntity.getAssertion(), key, value));
        testCaseEntity.setDbAssertion(ReplaceUtil.replace(testCaseEntity.getDbAssertion(), key, value));
        interfaceEntity.setPath(ReplaceUtil.replace(interfaceEntity.getPath(), key, value));
    }

    /**
     * 对返回作响应提取
     *
     * @param testCaseEntity
     * @param responseData
     */
    private List<TestResultCaseCommonDto> extractData(TestCaseEntity testCaseEntity, JSONObject responseData, TestRecordEntity testRecordEntity) {
        List<TestResultCaseCommonDto> testResultCaseCommonDtos = new ArrayList<>();
        Boolean extractResult = true;

        if (testCaseEntity.getExtract() != null && testCaseEntity.getExtract().isEmpty() == false) {
            JSONArray extracts = JSONObject.parseArray(testCaseEntity.getExtract());
            if (extracts != null && extracts.size() > 0) {
                Map<String, Object> extractCache = new HashMap<>();
                for (int i = 0; i < extracts.size(); i++) {
                    //默认提取结果为真
                    extractResult = true;
                    //每个响应结果都保存原有响应提取表达式和响应提取结果
                    TestResultCaseCommonDto testResultCaseCommonDto = new TestResultCaseCommonDto();
                    testResultCaseCommonDto.setAssertExpression(extracts.getJSONArray(i).toJSONString());

                    //处理当前响应提取
                    JSONArray extract = extracts.getJSONArray(i);
                    if (extract != null && extract.size() >= 2) {
                        //响应提取的Key，将作为缓存key
                        String cacheKey = extract.get(0).toString();
                        //响应提取的JsonPath
                        String jsonPath = extract.get(1).toString();
                        Object result = null;
                        try {
                            result = JSONPath.read(responseData.toJSONString(), jsonPath);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        //如果提取到值，则将义存入缓存
                        if (result.toString() != "[]" && responseData != null) {
                            if (result instanceof ArrayList) {
                                ArrayList arrResult = (ArrayList) result;
                                if (arrResult.size() > 0) {
                                    extractCache.put(cacheKey, arrResult.get(0));
                                }
                            } else if (result instanceof JSONArray) {
                                JSONArray arrResult = (JSONArray) result;
                                if (arrResult.size() > 0) {
                                    extractCache.put(cacheKey, arrResult.get(0));
                                }
                            }
                        } else {
                            extractResult = false;
                            testResultCaseCommonDto.setResult(extractResult);
                            TestResultCaseGetCommonDto.getAssertBody=false;
                            testResultCaseCommonDto.setRealValue(null);

                        }
                        testResultCaseCommonDto.setResult(extractResult);
                        testResultCaseCommonDto.setRealValue(JSON.toJSONString(extractCache));
                    }

                    testResultCaseCommonDtos.add(testResultCaseCommonDto);
                }
                //将响应提取存到的值添加到当前测试记录缓存
                this.addCacheItem(testRecordEntity.getTaskId(), testRecordEntity.getId(), extractCache);
            }
        }

        return testResultCaseCommonDtos;
    }

    /**
     * 对返回做用例断言
     *
     * @param testCaseEntity
     * @param responseData
     */
    private List<TestResultCaseCommonDto> assertResponse(TestCaseEntity testCaseEntity, JSONObject responseData) {
        List<TestResultCaseCommonDto> testResultCaseCommonDtos = new ArrayList<>();

        if (testCaseEntity.getAssertion() != null && testCaseEntity.getAssertion().isEmpty() == false) {
            JSONArray assertions = JSONObject.parseArray(testCaseEntity.getAssertion());
            if (assertions != null && assertions.size() > 0) {
                for (int i = 0; i < assertions.size(); i++) {
                    JSONArray assertion = assertions.getJSONArray(i);
                    TestResultCaseCommonDto testResultCaseCommonDto = this.assertResponseCompare(assertion, responseData);
                    testResultCaseCommonDtos.add(testResultCaseCommonDto);
                }
            }
        }

        return testResultCaseCommonDtos;
    }

    /**
     * 用例断言比较
     *
     * @param assertion
     * @param responseData
     * @return
     */
    private TestResultCaseCommonDto assertResponseCompare(JSONArray assertion, JSONObject responseData) {
        TestResultCaseCommonDto testResultCaseCommonDto = new TestResultCaseCommonDto();
        testResultCaseCommonDto.setAssertExpression(assertion.toJSONString());
        if (assertion != null && assertion.size() >= 3) {
            String operator = assertion.get(0).toString();
            String jsonPath = assertion.get(1).toString();
            String expectValue = assertion.get(2).toString();
            Object matchObject = null;

            ArrayList matchObjects = new ArrayList();
            //jsonpath默认提取成功
            boolean extractResult = true;

            try {
                matchObject = JSONPath.read(responseData.toJSONString(), jsonPath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (matchObject.toString() != "[]" && responseData != null) {
                if (matchObject instanceof ArrayList) {
                    ArrayList arrResult = (ArrayList) matchObject;
                    if (arrResult.size() > 0) {
                        matchObjects.add(arrResult.get(0));
                    }
                } else if (matchObject instanceof JSONArray) {
                    JSONArray arrResult = (JSONArray) matchObject;
                    if (arrResult.size() > 0) {
                        matchObjects.add(arrResult.get(0));
                    }
                }
                switch (operator) {
                    case "eq":
                        boolean assertResult = expectValue.equals(matchObjects.get(0).toString());
                        if (assertResult == false) {
                            testResultCaseCommonDto.setRealValue(matchObjects.get(0).toString());
                        }
                        testResultCaseCommonDto.setResult(assertResult);
                        break;
                    default:
                        break;
                }
            } else {
                extractResult = false;
                testResultCaseCommonDto.setResult(extractResult);
                TestResultCaseGetCommonDto.getAssertCase=false;
                testResultCaseCommonDto.setRealValue("用例提取失败");
            }
            testResultCaseCommonDto.setResult(extractResult);
            testResultCaseCommonDto.setRealValue(matchObjects.get(0).toString());

            if(TestResultCaseGetCommonDto.getAssertBody==false){
                testResultCaseCommonDto.setResult(false);
                testResultCaseCommonDto.setRealValue("#########【响应提取失败、用例断言成功】#########");
                TestResultCaseGetCommonDto.getAssertBody=true;
            }

        }

        return testResultCaseCommonDto;
    }

    /**
     * 对返回做数据库断言
     *
     * @param testCaseEntity
     * @return
     */
    private List<TestResultCaseCommonDto> dbAssertResponse(TestCaseEntity testCaseEntity, String dbConfig) {
        List<TestResultCaseCommonDto> testResultCaseCommonDtos = new ArrayList<>();

        //如果数据库断言配置不为空，则进行数据库断言
        if (testCaseEntity.getDbAssertion() != null && testCaseEntity.getDbAssertion().isEmpty() == false) {
            JSONArray assertions = JSONObject.parseArray(testCaseEntity.getDbAssertion());
            if (assertions != null && assertions.size() > 0) {
                for (int i = 0; i < assertions.size(); i++) {
                    JSONArray assertion = assertions.getJSONArray(i);
                    //对每个数据库断言进行比较
                    TestResultCaseCommonDto testResultCaseCommonDto = this.dbAssertResponseCompare(assertion, dbConfig);
                    testResultCaseCommonDtos.add(testResultCaseCommonDto);
                }
            }
        }

        return testResultCaseCommonDtos;
    }

    /**
     * 数据库断言比较
     *
     * @param assertion
     * @return
     */
    private TestResultCaseCommonDto dbAssertResponseCompare(JSONArray assertion, String dbConfig) {
        TestResultCaseCommonDto testResultCaseCommonDto = new TestResultCaseCommonDto();
        testResultCaseCommonDto.setAssertExpression(assertion.toJSONString());

        if (assertion != null && assertion.size() >= 3) {
            String operator = assertion.get(0).toString();
            String sql = assertion.get(1).toString();
            String expectValue = assertion.get(2).toString();

            JSONObject dbConfigData = JSONObject.parseObject(dbConfig);
            HikariCPUtil.jdbcUrl = dbConfigData.get("host").toString() + "/" + dbConfigData.get("db") + "?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useServerPrepStmts=true&cachePrepStmts=true&rewriteBatchedStatements=true";
            HikariCPUtil.jdbcUsername = dbConfigData.get("user").toString();
            HikariCPUtil.jdbcPassword = dbConfigData.get("user").toString();
            Object dbResult = null;
            switch (operator) {
                case "eq":
                    dbResult = HikariCPUtil.run(sql);
                    if (dbResult != null) {
                        boolean assertResult = dbResult.toString().equals(expectValue);
                        testResultCaseCommonDto.setResult(assertResult);
                        if (assertResult == false) {
                            testResultCaseCommonDto.setRealValue(dbResult.toString());
                        }
                    }
                    break;
                case "exist":
                    dbResult = HikariCPUtil.run(sql);
                    if (dbResult != null) {
                        boolean assertResult = dbResult.toString().equals("1");
                        testResultCaseCommonDto.setResult(assertResult);
                        if (assertResult == false) {
                            testResultCaseCommonDto.setRealValue(dbResult.toString());
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        return testResultCaseCommonDto;
    }

    /**
     * 添加缓存项目
     *
     * @param taskId
     * @param testRecordId
     * @param cacheValue
     */
    private void addCacheItem(Integer taskId, Integer testRecordId, Map<String, Object> cacheValue) {
        String cacheName = String.format(GLOBAL_CACHE_NAME, taskId);

        Map<String, Object> cacheResult = null;
        Object result = cacheManager.getCache(cacheName).get(testRecordId);
        if (result != null) {
            cacheResult = (Map<String, Object>) ((Cache.ValueWrapper) result).get();
        }
        if (cacheResult != null && cacheResult.size() > 0) {
            if (cacheValue != null && cacheValue.size() > 0) {
                for (String key : cacheValue.keySet()) {
                    cacheResult.put(key, cacheValue.get(key));
                }
            }
        } else {
            cacheResult = cacheValue;
        }
        cacheManager.getCache(cacheName).put(testRecordId, cacheResult);
    }
}
