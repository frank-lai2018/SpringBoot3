package com.frank.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AppKafkaConfiguration {
    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name("frank-topic1")
                .partitions(1)
                .compact()
                .build();
    }
}
