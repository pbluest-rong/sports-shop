package com.pblues.sportsshop.service.payment;

import com.pblues.sportsshop.dto.response.CreatePaymentResponse;

import java.math.BigDecimal;

public interface PaymentService {
    CreatePaymentResponse createPaymentQR(com.pblues.sportsshop.model.Order order, String extraData);

    void refundPayment(String momoTransactionId, BigDecimal depositAmount);
}
