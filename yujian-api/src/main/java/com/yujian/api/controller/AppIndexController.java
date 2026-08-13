package com.yujian.api.controller;

import com.yujian.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端健康检查 / 示例接口
 */
@RestController
@RequestMapping("/app")
public class AppIndexController {

    @GetMapping("/health")
    public R<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<String, Object>(4);
        data.put("service", "yujian-api");
        data.put("status", "UP");
        return R.ok(data);
    }
}
