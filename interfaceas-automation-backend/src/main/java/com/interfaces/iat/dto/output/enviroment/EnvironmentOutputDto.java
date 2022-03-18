package com.interfaces.iat.dto.output.enviroment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
public class EnvironmentOutputDto implements Serializable {
    @ApiModelProperty(value = "环境ID")
    private Integer id;
    @ApiModelProperty(value = "环境名称")
    private String name;
    @ApiModelProperty(value = "主机")
    private String host;
    @ApiModelProperty(value = "数据库配置")
    private String dbConfig;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "所属项目ID")
    private Integer projectId;
}
