package com.yujian.listener.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String DEMO_QUEUE = "yujian.demo.queue";

    @Bean
    public Queue demoQueue() {
        return new Queue(DEMO_QUEUE, true);
    }
}
