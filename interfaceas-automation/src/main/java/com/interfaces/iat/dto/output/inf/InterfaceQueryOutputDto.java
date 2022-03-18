package com.interfaces.iat.dto.output.inf;

import com.interfaces.iat.dto.output.module.ModuleOutputDto;
import lombok.Data;

import java.io.Serializable;


@Data
public class InterfaceQueryOutputDto implements Serializable {
    private Integer id;
    private String name;
    private String path;
    private String requestMethod;
    private String responseType;
    private Integer developerId;
    private String developerName;
    private Integer moduleId;
    private Integer projectId;
    private ModuleOutputDto module;
}
