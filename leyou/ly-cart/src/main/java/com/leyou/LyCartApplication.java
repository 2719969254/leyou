package com.leyou;

import com.leyou.cart.config.Jwtproperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2019/2/5
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableConfigurationProperties(Jwtproperties.class)
public class LyCartApplication {
	public static void main(String[] args) {
		SpringApplication.run(LyCartApplication.class, args);
	}
}
