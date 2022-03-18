package com.interfaces.iat.dto.input.job;

import lombok.Data;


@Data
public class XxlJobInputDto {
    private String name;
    private String cron;
    private String callbackUrl;
}