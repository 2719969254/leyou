package com.leyou.getway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/14
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
public class LyApiGateway {
	public static void main(String[] args) {
		SpringApplication.run(LyApiGateway.class, args);
	}
}
