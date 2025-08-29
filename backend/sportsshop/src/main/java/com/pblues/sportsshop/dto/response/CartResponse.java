package com.pblues.sportsshop.dto.response;

import lombok.Data;

import java.util.Set;

@Data
public class CartResponse {
    private Set<CartItemResponse> items;
}
