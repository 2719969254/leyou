package com.leyou.auth;

import com.leyou.auth.properties.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2019/1/22
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients
@EnableConfigurationProperties(value = JwtProperties.class)
public class LyAuthApplication {
	public static void main(String[] args) {
		SpringApplication.run(LyAuthApplication.class, args);
	}
}