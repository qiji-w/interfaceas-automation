package com.interfaces.iat.dto.output.testcase;

import lombok.Data;
import java.io.Serializable;


@Data
public class TestCaseOutputDto implements Serializable {
    private Integer id;
    private String name;
    private String requestData;
    private String marks;
    private String assertion;
    private String dbAssertion;
    private String extract;
    private Integer orderIndex;
    private Integer interfaceId;
    private Integer moduleId;
    private Integer testSuitId;
    private Integer taskId;
    private Integer projectId;
    private String description;
}
