package com.yujian.xxl.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class XxlIndexController {

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @GetMapping("/")
    public Map<String, Object> index() {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("module", "yujian-xxl");
        map.put("message", "请部署官方 xxl-job-admin 作为调度中心");
        map.put("adminAddresses", adminAddresses);
        return map;
    }
}
