package com.yujian.api.controller;

import com.yujian.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端（C 端）健康检查与示例接口。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@RestController
@RequestMapping("/app")
public class AppIndexController {

    /**
     * 探测 yujian-api 服务是否可用。
     *
     * @return 统一响应，data 为 Map：service 为模块名（yujian-api），status 为运行状态（UP 表示正常）
     */
    @GetMapping("/health")
    public R<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<String, Object>(4);
        data.put("service", "yujian-api");
        data.put("status", "UP");
        return R.ok(data);
    }
}
