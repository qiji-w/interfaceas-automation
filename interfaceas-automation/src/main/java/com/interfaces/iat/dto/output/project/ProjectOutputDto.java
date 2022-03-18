package com.interfaces.iat.dto.output.project;

import com.interfaces.iat.dto.output.BaseOutputDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ProjectOutputDto extends BaseOutputDto {
    @ApiModelProperty(value = "项目ID",position = 1,example = "99")
    private Integer id;
    @ApiModelProperty(value = "项目名",position = 1,example = "前程贷")
    private String name;
    @ApiModelProperty(value = "负责人Id",position = 2,example = "1")
    private Integer leaderId;
    @ApiModelProperty(value = "负责人姓名",position = 2,example = "李某某")
    private String leaderName;
    @ApiModelProperty(value = "描述",position = 3,example = "这是一个有前途的项目。")
    private String description;
}
