package com.kfzx.leyou.page;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients


/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/12/25
 */
public class LeyouGoodsWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeyouGoodsWebApplication.class, args);
    }
}