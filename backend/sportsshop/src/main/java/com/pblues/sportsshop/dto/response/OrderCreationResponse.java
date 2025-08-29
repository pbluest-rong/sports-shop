package com.pblues.sportsshop.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreationResponse {
    private OrderResponse order;
    private CreatePaymentResponse payment;
}