package com.pblues.sportsshop.kafka.product;

import com.pblues.sportsshop.common.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(ProductEvent productEvent) {
        kafkaTemplate.send(
                KafkaConfig.PRODUCT_TOPIC,
                productEvent.getProductId(),
                productEvent
        );
    }
}