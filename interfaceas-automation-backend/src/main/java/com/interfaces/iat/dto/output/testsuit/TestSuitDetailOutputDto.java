package com.interfaces.iat.dto.output.testsuit;

import com.interfaces.iat.dto.output.testcase.TestCaseOutputDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class TestSuitDetailOutputDto implements Serializable {
    private Integer id;
    private String name;
    private Integer taskId;
    private Integer projectId;
    private List<TestCaseOutputDto> testCases;
}
