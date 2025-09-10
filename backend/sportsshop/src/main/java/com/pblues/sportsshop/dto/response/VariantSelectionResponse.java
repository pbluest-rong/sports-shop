package com.pblues.sportsshop.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VariantSelectionResponse {
    private String lastAttributeName; // tên attribute cuối
    private List<AttributeOption> lastAttributeOptions; // stock cho attribute cuối
    private VariantResponse variant; // nếu chọn đủ, trả variant chính xác, ngược lại null

    @Data
    @Builder
    public static class AttributeOption {
        private Object value;
        private int stock;
    }
}
