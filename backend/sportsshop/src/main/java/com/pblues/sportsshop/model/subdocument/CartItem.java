package com.pblues.sportsshop.model.subdocument;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CartItem {
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String productId;
    private String sku;
    private int quantity;
}