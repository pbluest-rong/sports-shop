package com.pblues.sportsshop.dto.response;

import com.pblues.sportsshop.common.constant.OrderStatus;
import com.pblues.sportsshop.common.constant.PaymentMethod;
import com.pblues.sportsshop.common.constant.ShippingMethod;
import com.pblues.sportsshop.model.Order;
import com.pblues.sportsshop.model.OrderItem;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    // Thông tin cơ bản
    private String id;
    private LocalDateTime orderDateTime;
    private OrderStatus status;

    private long userId;

    // Địa chỉ giao hàng
    private String firstName;
    private String lastName;
    private String phone;
    private String fullAddress;
    private String addressNote;

    // Chi tiết đơn hàng
    private Set<OrderItem> items = new HashSet<>();
    private BigDecimal orderAmount;
    private BigDecimal deliveryAmount;
    private BigDecimal totalAmount;

    // Phương thức thanh toán & vận chuyển
    private PaymentMethod paymentMethod;
    private ShippingMethod shippingMethod;

    // Ghi chú và lý do hủy
    private String note;
    private String cancellationReason;

    public static OrderResponse mapperToOrderResponse(Order order){
        return OrderResponse.builder()
                .id(order.getId())
                .items(order.getItems())
                .orderDateTime(order.getOrderDateTime())
                .status(order.getStatus())
                .userId(order.getUser().getId())
                .firstName(order.getFirstName())
                .lastName(order.getLastName())
                .phone(order.getPhone())
                .fullAddress(order.getFullAddress())
                .addressNote(order.getAddressNote())
                .orderAmount(order.getOrderAmount())
                .deliveryAmount(order.getDeliveryAmount())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .shippingMethod(order.getShippingMethod())
                .note(order.getNote())
                .cancellationReason(order.getCancellationReason())
                .build();
    }
}

