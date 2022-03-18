package com.interfaces.iat.dto.output;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class BaseOutputDto implements Serializable {
    private Integer createById;
    private String createByName;
    private Date createTime;
    private Integer updateById;
    private String updateByName;
    private Date updateTime;
}
