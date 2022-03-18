package com.interfaces.iat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName("tb_test_record")
public class TestRecordEntity implements Serializable {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String name;
    private Integer status;
    private Boolean isDelete;
    private Integer taskId;
    private Integer projectId;
    private Integer environmentId;

    private Integer createById;
    private String createByName;
    private Date createTime;
    private Integer updateById;
    private String updateByName;
    private Date updateTime;
}
