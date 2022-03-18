package com.interfaces.iat.dto.input.testsuit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@ApiModel(value = "测试套件创建输入DTO",description = "描述测试套件信息")
@Data
public class TestSuitCreateInputDto {
    @ApiModelProperty(value = "测试套件名称",position = 1,example = "借贷测试套件")
    @NotNull(message = "测试套件名称不能为空")
    private String name;
    @ApiModelProperty(value = "所属任务ID",position = 2,example = "99")
    @NotNull(message = "所属任务ID不能为空")
    private Integer taskId;
    @ApiModelProperty(value = "所属项目ID",position = 2,example = "99")
    @NotNull(message = "所属项目ID不能为空")
    private Integer projectId;
}
