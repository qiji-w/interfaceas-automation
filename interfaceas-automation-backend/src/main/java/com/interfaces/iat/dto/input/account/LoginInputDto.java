package com.interfaces.iat.dto.input.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@ApiModel(value = "登录输入DTO",description = "描述登录信息")
@Data
public class LoginInputDto {
    @ApiModelProperty(value = "用户名",position = 1,example = "xiaoming")
    @NotNull(message = "用户名不能为空" )
    private String username;
    @ApiModelProperty(value = "密码",position = 2,example = "ILove99^")
    @NotNull(message = "密码不能为空" )
    @Length(min = 6,max = 20,message = "密码长度必须在6-20个字符之间")
    private String password;
}
