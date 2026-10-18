package com.online.learning.registration_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic registrationEventsTopic() {
        return TopicBuilder.name("registration-events")
                .partitions(3)
                .replicas(1)      // 1 because you have a single local broker
                .build();
    }
}