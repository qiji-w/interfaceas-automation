package com.interfaces.iat.dto.input.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel(value = "切换任务是否定时执行Dto",description = "切换任务是否定时执行Dto")
@Data
public class ChangeIsJobOrNotInputDto {
    @ApiModelProperty(value = "测试任务ID",position = 1,example = "99")
    @NotNull(message = "测试任务ID不能为空")
    private Integer taskId;
    @ApiModelProperty(value = "是否定时执行",position = 2,example = "true")
    @NotNull(message = "是否定时执行不能为空")
    private Boolean isJob;
}
