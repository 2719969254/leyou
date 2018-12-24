package com.kfzx.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author MR.Tian
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class LySearchService {
	public static void main(String[] args) {
		SpringApplication.run(LySearchService.class, args);
	}
}