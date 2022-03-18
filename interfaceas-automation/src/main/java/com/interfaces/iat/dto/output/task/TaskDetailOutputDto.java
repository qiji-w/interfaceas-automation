package com.interfaces.iat.dto.output.task;

import com.interfaces.iat.dto.output.testsuit.TestSuitDetailOutputDto;
import lombok.Data;

import java.util.List;


@Data
public class TaskDetailOutputDto {
    private Integer id;
    private String name;
    private String description;
    private Integer projectId;
    private Boolean isArchive;
    private Integer archiveId;
    private String archiveName;
    private Boolean isJob;
    private List<TestSuitDetailOutputDto> testSuits;
}
