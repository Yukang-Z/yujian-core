package com.yujian.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan(basePackages = {"com.yujian"})
public class YujianTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(YujianTaskApplication.class, args);
        System.out.println("======== 宇健口腔 Task 服务启动成功 ========");
    }
}
