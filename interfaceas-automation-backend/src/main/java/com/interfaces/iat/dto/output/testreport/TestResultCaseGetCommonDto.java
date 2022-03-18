package com.interfaces.iat.dto.output.testreport;

import lombok.Data;


@Data
public class TestResultCaseGetCommonDto {

    /**
     * 响应提取结果
     */
    public static Boolean getAssertBody=true;
    /**
     * 用例提取结果
     */
    public static Boolean getAssertCase=true;
    /**
     * 用例断言结果
     */
    public static Boolean assertCase=true;

}
