package com.yujian.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class YujianEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(YujianEurekaApplication.class, args);
        System.out.println("======== 宇健口腔 Eureka 注册中心启动成功 ========");
    }
}
