package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 启动类
 *
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/14
 */

@SpringBootApplication
@EnableEurekaServer
public class LyRegistry {
	public static void main(String[] args) {
		SpringApplication.run(LyRegistry.class, args);
	}
}
