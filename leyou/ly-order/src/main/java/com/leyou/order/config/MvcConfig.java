package com.leyou.order.config;

import com.leyou.order.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2019/1/25
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class MvcConfig implements WebMvcConfigurer {

    private final JwtProperties jwtProperties;

    @Autowired
    public MvcConfig(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor(jwtProperties);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePath = new ArrayList<>();
        excludePath.add("/swagger-ui.html");
        excludePath.add("/swagger-resources/**");
        excludePath.add("/webjars/**");

        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/**").excludePathPatterns(excludePath);
    }
}