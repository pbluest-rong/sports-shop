package com.pblues.sportsshop.service.order;

import com.pblues.sportsshop.dto.request.OrderRequest;
import com.pblues.sportsshop.dto.response.OrderCreationResponse;
import org.springframework.security.core.Authentication;

public interface OrderService {
    OrderCreationResponse checkout(Authentication auth, OrderRequest request);
    void handlePaymentCallback(String orderId, String paymentTransactionId);
}