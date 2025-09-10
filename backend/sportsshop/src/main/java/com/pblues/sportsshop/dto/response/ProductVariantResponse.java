package com.pblues.sportsshop.dto.response;

import com.pblues.sportsshop.model.subdocument.Variant;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class ProductVariantResponse {
    private Map<String, List<AttributeOption>> attributes;
    private VariantResponse defaultVariant;
    @Data
    @Builder
    public static class AttributeOption {
        private Object value;
        private String image;
        private Integer stock;
    }
}