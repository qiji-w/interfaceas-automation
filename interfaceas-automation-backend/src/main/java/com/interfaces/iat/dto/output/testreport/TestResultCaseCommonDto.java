package com.interfaces.iat.dto.output.testreport;

import lombok.Data;


@Data
public class TestResultCaseCommonDto {
    /**
     * 表达式，分别为响应提取、用例断言、数据库断言时的表达式
     */
    private String assertExpression;
    /**
     * 响应提取、断言结果
     */
    private  Boolean result;
    /**
     * 真实值，如果断言结果为false，保存真实值
     */
    private String realValue;

}
