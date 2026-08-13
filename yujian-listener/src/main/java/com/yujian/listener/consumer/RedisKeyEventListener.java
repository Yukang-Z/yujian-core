package com.yujian.listener.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Redis Key 事件监听器占位组件
 * <p>
 * 后续可配合 {@code RedisMessageListenerContainer} 订阅 {@code __keyevent@*__:*} 等频道，
 * 实现键过期、删除等事件的业务处理。
 * </p>
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Component
public class RedisKeyEventListener {

    private static final Logger log = LoggerFactory.getLogger(RedisKeyEventListener.class);

    /**
     * 初始化占位监听器，提示后续接入 Redis 键空间通知
     */
    @PostConstruct
    public void init() {
        log.info("RedisKeyEventListener 已就绪，待接入 Redis 键空间事件订阅");
    }

    /**
     * 处理 Redis Key 事件（占位方法）
     *
     * @param channel 订阅频道
     * @param message 事件消息
     */
    public void onMessage(String channel, String message) {
        log.info("收到 Redis Key 事件, channel={}, message={}", channel, message);
    }

    /*
     * 示例：启用键空间通知后可注册 RedisMessageListenerContainer
     *
     * @Bean
     * RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory factory) {
     *     RedisMessageListenerContainer container = new RedisMessageListenerContainer();
     *     container.setConnectionFactory(factory);
     *     container.addMessageListener((message, pattern) -> {
     *         onMessage(new String(message.getChannel()), new String(message.getBody()));
     *     }, new PatternTopic("__keyevent@0__:*"));
     *     return container;
     * }
     */
}
