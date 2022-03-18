package com.interfaces.iat.dto.input.inf;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@ApiModel(value = "接口创建输入DTO",description = "描述接口信息")
@Data
public class InterfaceCreateInputDto {
    @ApiModelProperty(value = "接口名称",position = 1,example = "用户注册接口")
    @NotNull(message = "接口名称不能为空")
    private String name;
    @ApiModelProperty(value = "接口路径",position = 1,example = "/member/register")
    @NotNull(message = "接口路径不能为空")
    @Length(min = 1,max=200,message = "接口路径长度必须在1-200之间")
    private String path;
    @ApiModelProperty(value = "请求方法",position = 1,example = "GET")
    @NotNull(message = "请求方法不能为空")
    private String requestMethod;
    @ApiModelProperty(value = "响应类型",position = 1,example = "JSON")
    @NotNull(message = "响应类型不能为空")
    private String responseType;
    @ApiModelProperty(value = "开发人员姓名",position = 1,example = "99")
    private Integer developerId;
    @ApiModelProperty(value = "开发人员姓名",position = 1,example = "张三")
    private String developerName;
    @ApiModelProperty(value = "描述",position = 1,example = "描述")
    private String description;
    @ApiModelProperty(value = "所属模块ID",position = 2,example = "99")
    @NotNull(message = "所属模块ID不能为空")
    private Integer moduleId;
    @ApiModelProperty(value = "所属项目ID",position = 2,example = "99")
    @NotNull(message = "所属项目ID不能为空")
    private Integer projectId;
}
