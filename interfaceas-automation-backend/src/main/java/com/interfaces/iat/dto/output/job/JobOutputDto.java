package com.interfaces.iat.dto.output.job;

import lombok.Data;

import java.io.Serializable;

@Data
public class JobOutputDto implements Serializable {
    private Integer id;
    private String name;
    private String cron;
    private Integer status;
    private String description;
    private Integer taskId;
    private Integer projectId;
    private Integer environmentId;
    private Integer xxlJobId;
}
