package com.interfaces.iat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Value("${swagger.host}")
    String swaggerHost;

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(buildApiInfo())
                .host(this.swaggerHost)
                .select()
                //.apis(RequestHandlerSelectors.any())
                //指定此package下的接口显示在接口文档中
                .apis(RequestHandlerSelectors.basePackage("com.interfaces.iat.controller"))
                .paths(PathSelectors.any())
                .build()
                ;
    }

    private ApiInfo buildApiInfo() {
        return new ApiInfoBuilder().
                //文档标题
                        title("《接口自动化测试平台》接口文档")
                //联系人
                .contact(new Contact("qi",null, null))
                //文档描述
                .description("《接口自动化测试平台》接口文档")
                //版本
                .version("1.0")
                .build();
    }
}
