package com.interfaces.iat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_task_module")
public class TaskModuleEntity implements Serializable {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private Integer taskId;
    private Integer moduleId;
}
