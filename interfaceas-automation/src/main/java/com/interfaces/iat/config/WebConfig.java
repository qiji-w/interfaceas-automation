package com.interfaces.iat.config;

import com.interfaces.iat.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    AuthenticationInterceptor authenticationInterceptor;

    /**
     * 添加拦截器和过滤规则，并排除部分swagger-ui的静态资源
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/account/**")
                .excludePathPatterns("/xxljobcallback/**")  //排除XXL-Job回调页面
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");
    }

    /**
     * 配置允许跨域
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)     //允许跨越发送cookie
                .allowedOriginPatterns("*")  //允许所有域名进行跨域调用
                .allowedHeaders("*")    //放行全部原始头信息
                .allowedMethods("*");   //允许所有请求方法跨域调用
    }
}
