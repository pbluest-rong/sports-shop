package com.pblues.sportsshop.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "order_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"productId", "sku"})
})
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String productId;
    @Column(nullable = false)
    private String sku;
    @Column(nullable = false)
    private BigDecimal unitPrice;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private BigDecimal totalPrice;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
