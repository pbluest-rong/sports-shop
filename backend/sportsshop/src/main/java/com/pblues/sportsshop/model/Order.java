package com.pblues.sportsshop.model;


import com.pblues.sportsshop.constant.OrderStatus;
import com.pblues.sportsshop.constant.PaymentMethod;
import com.pblues.sportsshop.constant.ShippingMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "orders")
public class Order {
    @Id
    private String id;
    @Column(nullable = false)
    private LocalDateTime orderDateTime;
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> items = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String fullAddress;
    private String addressNote;

    @Column(nullable = false)
    private BigDecimal orderAmount;
    @Column(nullable = false)
    private BigDecimal deliveryAmount;
    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private PaymentMethod paymentMethod;
    @Column(nullable = false)
    private ShippingMethod shippingMethod;

    private String note;
    private String cancellationReason;
}
