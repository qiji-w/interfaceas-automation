package com.interfaces.iat.dto.output.task;

import com.interfaces.iat.dto.output.job.JobOutputDto;
import com.interfaces.iat.dto.output.module.ModuleOutputDto;
import lombok.Data;

import java.util.List;


@Data
public class TaskQueryOutputDto {
    private Integer id;
    private String name;
    private String description;
    private Integer projectId;
    private Boolean isArchive;
    private Integer archiveId;
    private String archiveName;
    private Boolean isJob;

    private List<ModuleOutputDto> modules;
    private List<JobOutputDto> jobs;
}
