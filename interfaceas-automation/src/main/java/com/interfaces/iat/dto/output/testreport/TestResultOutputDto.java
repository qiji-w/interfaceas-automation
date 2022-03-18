package com.interfaces.iat.dto.output.testreport;

import com.interfaces.iat.dto.output.enviroment.EnvironmentOutputDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class TestResultOutputDto implements Serializable {
    private Integer taskId;
    private String taskName;
    private Double totalDuration;
    private Integer status;
    private Long totalOfTestSuit;
    private Long totalOfTestCase;
    private Long totalOfTestCaseForSuccess;
    private Long totalOfTestCaseForFailure;
    private Long totalOfTestCaseForError;

    private List<TestResultSuitOutputDto> testSuitResults;
    private EnvironmentOutputDto environment;
}
