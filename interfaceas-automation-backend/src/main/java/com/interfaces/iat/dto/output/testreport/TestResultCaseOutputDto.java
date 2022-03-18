package com.interfaces.iat.dto.output.testreport;

import com.interfaces.iat.dto.output.inf.InterfaceOutputDto;
import com.interfaces.iat.dto.output.testcase.TestCaseOutputDto;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
public class TestResultCaseOutputDto implements Serializable {
    /**
     * 测试用例
     */
    private TestCaseOutputDto testCase;
    /**
     * 对应接口
     */
    private InterfaceOutputDto inf;
    /**
     * 接口调用开始时间
     */
    private Date startTime;
    /**
     * 接口调用结束时间
     */
    private Date endTime;
    /**
     * 状态，0表示成功，1表示异常，2表示错误
     */
    private Integer status;
    /**
     * 返回内容
     */
    private String responseData;
    /**
     * 异常信息，如果调用异常的话
     */
    private String exception;

    /**
     * 响应提取
     */
    private List<TestResultCaseCommonDto> extracts;

    /**
     * 断言
     */
    private List<TestResultCaseCommonDto> asserts;
    /**
     * 数据库断言
     */
    private List<TestResultCaseCommonDto> dbAsserts;
}
