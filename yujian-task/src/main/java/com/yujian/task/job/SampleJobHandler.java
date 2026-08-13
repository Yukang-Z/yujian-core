package com.yujian.task.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 示例定时任务处理器
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Component
public class SampleJobHandler {

    private static final Logger log = LoggerFactory.getLogger(SampleJobHandler.class);

    /**
     * 示例任务：记录开始/结束日志并上报执行结果
     */
    @XxlJob("sampleJobHandler")
    public void sampleJobHandler() {
        long startTime = System.currentTimeMillis();
        log.info("sampleJobHandler 开始执行");

        try {
            // 占位业务逻辑，后续替换为实际定时任务处理
            log.info("sampleJobHandler 执行完成, 耗时: {} ms", System.currentTimeMillis() - startTime);
            XxlJobHelper.handleSuccess("sampleJobHandler 执行成功");
        } catch (Exception e) {
            log.error("sampleJobHandler 执行失败, 耗时: {} ms", System.currentTimeMillis() - startTime, e);
            XxlJobHelper.handleFail("sampleJobHandler 执行失败: " + e.getMessage());
        }
    }
}
