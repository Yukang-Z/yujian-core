package com.yujian.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class YujianGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(YujianGatewayApplication.class, args);
        System.out.println("======== 宇健口腔 Gateway 网关启动成功 ========");
    }
}
