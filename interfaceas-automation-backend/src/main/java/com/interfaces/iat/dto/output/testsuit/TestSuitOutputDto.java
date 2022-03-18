package com.interfaces.iat.dto.output.testsuit;

import lombok.Data;

import java.io.Serializable;


@Data
public class TestSuitOutputDto implements Serializable {
    private Integer id;
    private String name;
    private Integer taskId;
    private Integer projectId;
}
