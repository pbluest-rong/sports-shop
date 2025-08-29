package com.pblues.sportsshop.dto.request;

import lombok.Data;

@Data
public class AddCartItemRequest {
    private String productId;
    private int quantity;
    private String sku;
}