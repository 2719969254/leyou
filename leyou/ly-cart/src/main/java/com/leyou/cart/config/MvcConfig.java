package com.leyou.cart.config;

import com.leyou.cart.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * @author VicterTian
 * @version V1.0
 * @Date 2019/1/25
 */
@Configuration
@EnableConfigurationProperties(Jwtproperties.class)
public class MvcConfig implements WebMvcConfigurer {

    private final Jwtproperties jwtProperties;

    @Autowired
    public MvcConfig(Jwtproperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor(jwtProperties);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor()).addPathPatterns("/**");
    }
}