package com.interfaces.iat.dto.output.job;

import lombok.Data;

import java.io.Serializable;


@Data
public class XxlJobOutputDto implements Serializable {
    private int code;
    private String msg;
    private String content;
}