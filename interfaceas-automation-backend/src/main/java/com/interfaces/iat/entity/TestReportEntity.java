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
@TableName("tb_test_report")
public class TestReportEntity implements Serializable {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String result;
    private String description;
    private Boolean isDelete;
    private Integer testRecordId;
    private Integer projectId;

    private Integer createById;
    private String createByName;
    private Date createTime;
    private Integer updateById;
    private String updateByName;
    private Date updateTime;
}
