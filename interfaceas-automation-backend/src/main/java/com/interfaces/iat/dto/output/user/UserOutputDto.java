package com.interfaces.iat.dto.output.user;

import com.interfaces.iat.dto.output.BaseOutputDto;
import lombok.Data;

import java.util.List;


@Data
public class UserOutputDto extends BaseOutputDto {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String mobile;
    private List<String> roles;
    private String description;
}
