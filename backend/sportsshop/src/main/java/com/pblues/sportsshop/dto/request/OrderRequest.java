package com.pblues.sportsshop.dto.request;

import com.pblues.sportsshop.constant.PaymentMethod;
import com.pblues.sportsshop.constant.ShippingMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {
    private BigDecimal totalPrice;
    private List<String> cartItemIds;
    private long addressId;
    private PaymentMethod paymentMethod;
    private ShippingMethod shippingMethod;
    private String note;
 }