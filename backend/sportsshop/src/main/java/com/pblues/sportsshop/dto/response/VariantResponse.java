package com.pblues.sportsshop.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class VariantResponse {
    private String id;
    private String sku;
    private Map<String, Object> attributes;
    private List<String> images;
    private int displayOrder;
    private BigDecimal price;
    private int stock;
}
