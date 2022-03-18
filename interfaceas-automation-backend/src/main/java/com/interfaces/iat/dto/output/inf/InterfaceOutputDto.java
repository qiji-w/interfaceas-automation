package com.interfaces.iat.dto.output.inf;

import lombok.Data;

import java.io.Serializable;


@Data
public class InterfaceOutputDto implements Serializable {
    private Integer id;
    private String name;
    private String path;
    private String requestMethod;
    private String responseType;
    private Integer developerId;
    private String developerName;
    private Integer moduleId;
    private Integer projectId;
}
