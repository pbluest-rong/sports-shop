package com.pblues.sportsshop.common.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    public static final String PRODUCT_TOPIC = "product-events";

    @Bean
    public NewTopic newProductTopic() {
        return TopicBuilder.name(PRODUCT_TOPIC)
                .partitions(3)
                .replicas(2)
                .build();
    }
}
