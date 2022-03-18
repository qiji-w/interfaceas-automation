package com.interfaces.iat.dto.output.testreport;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class TestResultSuitOutputDto implements Serializable {
    private Integer testSuitId;
    private String testSuitName;

    private List<TestResultCaseOutputDto> testCaseResults;
}
