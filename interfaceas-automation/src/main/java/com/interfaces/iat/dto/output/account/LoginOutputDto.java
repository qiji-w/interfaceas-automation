package com.interfaces.iat.dto.output.account;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class LoginOutputDto implements Serializable {
    private Integer id;
    private String username;
    private String name;
    private List<String> roles;
}
