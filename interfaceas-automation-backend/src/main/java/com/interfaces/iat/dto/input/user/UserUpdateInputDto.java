package com.interfaces.iat.dto.input.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class UserUpdateInputDto {
    private Integer id;
    private String username;
    @NotNull(message = "姓名不能为空")
    private String name;
    private String email;
    private String mobile;
    @NotEmpty(message = "角色不能为空")
    private List<String> roles;
    private String description;

}
