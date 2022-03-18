package com.interfaces.iat.dto.input.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class UserResetPasswordInputDto {
    @ApiModelProperty(value = "ID",example = "1")
    @NotNull(message = "用户ID不能为空")
    private Integer id;
    @ApiModelProperty(value = "密码")
    @NotNull(message = "密码不能为空")
    @Length(min = 6,max = 20,message = "密码长度必须在6-20个字符之间")
    private String password;
}
