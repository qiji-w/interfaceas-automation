package com.interfaces.iat.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        //添加此配置，让没有主键的转换不会抛出异常
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper;
    }
}
