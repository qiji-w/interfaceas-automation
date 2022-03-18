package com.interfaces.iat.dto.input.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@ApiModel(value = "模块修改输入DTO",description = "描述模块信息")
@Data
public class ModuleUpdateInputDto {
    @ApiModelProperty(value = "模块ID",position = 1,example = "99")
    @NotNull(message = "模块ID不能为空")
    private Integer id;
    @ApiModelProperty(value = "模块名称",position = 1,example = "借贷模块")
    @NotNull(message = "模块名称不能为空")
    private String name;
    @ApiModelProperty(value = "描述",position = 1,example = "描述")
    private String description;
    @ApiModelProperty(value = "所属项目ID",position = 2,example = "99")
    @NotNull(message = "所属项目ID不能为空")
    private Integer projectId;
}
