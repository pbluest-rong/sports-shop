package com.pblues.sportsshop.controller;

import com.pblues.sportsshop.dto.request.OrderRequest;
import com.pblues.sportsshop.dto.response.ApiResponse;
import com.pblues.sportsshop.dto.response.OrderCreationResponse;
import com.pblues.sportsshop.service.OrderService;
import com.pblues.sportsshop.dto.request.MomoIpnRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse> checkout(Authentication auth, OrderRequest request) {
        OrderCreationResponse response = orderService.checkout(auth, request);
        return ResponseEntity.ok().body(ApiResponse.success("success", response));
    }

    @PostMapping("/momo/ipn-handler")
    public void ipnHandler(@RequestBody MomoIpnRequest request) {
        if (request.getResultCode() == 0) {
            orderService.handlePaymentCallback(request.getOrderId(), String.valueOf(request.getTransId()));
        }
    }
}
