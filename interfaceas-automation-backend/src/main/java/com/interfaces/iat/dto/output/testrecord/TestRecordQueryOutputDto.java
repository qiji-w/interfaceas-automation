package com.interfaces.iat.dto.output.testrecord;

import com.interfaces.iat.dto.output.BaseOutputDto;
import com.interfaces.iat.dto.output.testreport.TestReportOutputDto;
import com.interfaces.iat.dto.output.task.TaskOutputDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel(value = "测试记录查询响应DTO",description = "描述测试记录信息")
@Data
public class TestRecordQueryOutputDto extends BaseOutputDto {
    @ApiModelProperty(value = "测试记录ID",position = 1,example = "99")
    private Integer id;
    @ApiModelProperty(value = "运行记录名称",position = 1,example = "0.1")
    private String name;
    @ApiModelProperty(value = "测试记录状态",position = 1)
    private Integer status;
    @ApiModelProperty(value = "所属任务ID",position = 4,example = "99")
    private Integer taskId;
    @ApiModelProperty(value = "所属项目ID",position = 4,example = "99")
    private Integer projectId;
    @ApiModelProperty(value = "所属环境ID",position = 5,example = "99")
    private Integer environmentId;

    private TaskOutputDto task;

    private TestReportOutputDto testReport;
}
