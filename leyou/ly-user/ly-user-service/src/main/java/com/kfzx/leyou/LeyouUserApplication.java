package com.kfzx.leyou;

import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2019/1/3
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.kfzx.leyou.mapper")
public class LeyouUserApplication {
	public static void main(String[] args) {
		SpringApplication.run(LeyouUserApplication.class, args);
	}
}