package com.yujian.xxl.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * XXL-JOB 执行器模块首页，返回模块说明与调度中心配置信息。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@RestController
public class XxlIndexController {

    /** 调度中心地址 */
    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    /**
     * 返回执行器模块说明及已配置的调度中心地址。
     *
     * @return 键值对 Map：module 为模块名（yujian-xxl），message 为部署提示，adminAddresses 为调度中心地址
     */
    @GetMapping("/")
    public Map<String, Object> index() {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("module", "yujian-xxl");
        map.put("message", "请部署官方 xxl-job-admin 作为调度中心");
        map.put("adminAddresses", adminAddresses);
        return map;
    }
}
