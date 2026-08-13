package com.yujian.task.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 定时任务示例
 */
@Slf4j
@Component
public class DemoJobHandler {

    @XxlJob("demoJobHandler")
    public void demoJobHandler() {
        log.info("执行示例定时任务 demoJobHandler");
    }
}
