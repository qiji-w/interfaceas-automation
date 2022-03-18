package com.interfaces.iat.dto.output.module;

import lombok.Data;
import java.io.Serializable;


@Data
public class ModuleOutputDto implements Serializable {
    private Integer id;
    private String name;
    private Integer projectId;
}
