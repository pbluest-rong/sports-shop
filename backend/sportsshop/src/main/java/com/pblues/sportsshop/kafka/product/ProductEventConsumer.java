package com.pblues.sportsshop.kafka.product;

import com.pblues.sportsshop.common.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductEventConsumer {
    private final ProductEventHandler productEventHandler;

    @KafkaListener(topics = KafkaConfig.PRODUCT_TOPIC, groupId = "product-consumers")
    public void consume(ProductEvent event) {
        productEventHandler.handle(event);
    }
}
