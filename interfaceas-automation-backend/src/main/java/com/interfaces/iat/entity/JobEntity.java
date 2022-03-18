package com.interfaces.iat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_job")
public class JobEntity extends BaseEntity {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String name;
    private String cron;
    private Integer status;
    private String description;
    private Boolean isDelete;
    private Integer taskId;
    private Integer projectId;
    private Integer environmentId;
    private Integer xxlJobId;
}
