package com.yujian.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.yujian.admin.mapper")
@ComponentScan(basePackages = {"com.yujian"})
public class YujianAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(YujianAdminApplication.class, args);
        System.out.println("======== 宇健口腔 Admin 服务启动成功 ========");
    }
}
