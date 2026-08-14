package com.yujian.listener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.yujian"})
public class YujianListenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YujianListenerApplication.class, args);
        System.out.println("======== 宇健口腔 Listener 服务启动成功 ========");
    }
}
