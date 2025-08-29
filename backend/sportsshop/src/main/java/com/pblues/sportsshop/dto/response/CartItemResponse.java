package com.pblues.sportsshop.dto.response;

import com.pblues.sportsshop.model.subdocument.Variant;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    private String productId;
    private String sku;

    private String imageUrl;
    private Variant variant;
}
