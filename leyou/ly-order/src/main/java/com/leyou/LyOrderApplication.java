package com.leyou;

import com.leyou.order.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.leyou.order.mapper")
@EnableConfigurationProperties(JwtProperties.class)
public class LyOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyOrderApplication.class,args);
    }
}
