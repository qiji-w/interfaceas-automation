package com.interfaces.iat.dto.output.task;

import lombok.Data;

import java.util.List;


@Data
public class TaskOutputDto {
    private Integer id;
    private String name;
    private String description;
    private Integer projectId;
    private Boolean isArchive;
    private Boolean isJob;
    private Integer archiveId;
    private String archiveName;

    private List<Integer> moduleIds;
}
