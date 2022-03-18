package com.interfaces.iat.dto.input.job;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel(value = "定时执行修改Dto",description = "定时执行修改Dto")
@Data
public class JobUpdateInputDto {
    @ApiModelProperty(value = "定时执行ID",position = 1)
    @NotNull(message = "定时执行ID不能为空")
    private Integer id;
    @ApiModelProperty(value = "定时执行名称",position = 2)
    @NotNull(message = "定时执行名称不能为空")
    private String name;
    @ApiModelProperty(value = "CRON表达式",position = 3)
    @NotNull(message = "CRON表达式不能为空")
    private String cron;
    @ApiModelProperty(value = "描述",position = 4)
    private String description;
    @ApiModelProperty(value = "所属任务ID",position = 5)
    @NotNull(message = "所属任务ID不能为空")
    private Integer taskId;
    @ApiModelProperty(value = "所属项目ID",position = 6)
    @NotNull(message = "所属项目ID不能为空")
    private Integer projectId;
    @ApiModelProperty(value = "所属项目环境ID",position = 7)
    @NotNull(message = "所属项目环境ID不能为空")
    private Integer environmentId;
}
