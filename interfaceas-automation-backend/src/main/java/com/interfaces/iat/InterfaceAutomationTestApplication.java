package com.interfaces.iat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
@MapperScan("com.interfaces.iat.mapper")
public class InterfaceAutomationTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterfaceAutomationTestApplication.class, args);
    }

}
