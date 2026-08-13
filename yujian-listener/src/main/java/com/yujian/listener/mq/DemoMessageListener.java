package com.yujian.listener.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消息监听示例（可按业务扩展队列）
 */
@Slf4j
@Component
public class DemoMessageListener {

    @RabbitListener(queues = "yujian.demo.queue", containerFactory = "rabbitListenerContainerFactory")
    public void onMessage(String message) {
        log.info("收到消息: {}", message);
    }
}
