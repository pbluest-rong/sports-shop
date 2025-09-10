package com.pblues.sportsshop.common.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class QdrantFeignConfig {
    @Value("${qdrant.apiKey}")
    private String apiKey;
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("api-key", apiKey);
        };
    }
}
