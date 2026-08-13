package com.yujian.xxl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * XXL-JOB 调度中心接入说明模块。
 * 正式环境建议直接部署官方 xxl-job-admin，本模块提供统一配置与健康检查入口。
 */
@SpringBootApplication
@EnableEurekaClient
public class YujianXxlApplication {

    public static void main(String[] args) {
        SpringApplication.run(YujianXxlApplication.class, args);
        System.out.println("======== 宇健口腔 XXL 调度接入模块启动成功 ========");
        System.out.println("提示：请单独部署 xxl-job-admin，默认地址 http://127.0.0.1:8085/xxl-job-admin");
    }
}
