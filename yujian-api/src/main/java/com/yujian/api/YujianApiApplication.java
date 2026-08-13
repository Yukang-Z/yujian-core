package com.yujian.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.yujian.api.mapper")
@ComponentScan(basePackages = {"com.yujian"})
public class YujianApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(YujianApiApplication.class, args);
        System.out.println("======== 宇健口腔 API 服务启动成功 ========");
    }
}
