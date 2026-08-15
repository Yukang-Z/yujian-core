package com.yujian.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka 注册中心启动类
 *
 * @author Zhangyk
 * @date 2026-08-15 10:10
 */
@SpringBootApplication
@EnableEurekaServer
public class YujianEurekaApplication {

    /**
     * 启动 Eureka Server
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(YujianEurekaApplication.class, args);
    }
}
