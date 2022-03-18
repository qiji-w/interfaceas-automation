package com.interfaces.iat.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {
    @TableField(fill = FieldFill.INSERT)
    private Integer createById;
    @TableField(fill = FieldFill.INSERT)
    private String createByName;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer updateById;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateByName;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
