package com.pblues.sportsshop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private Integer provinceId;

    @Column(nullable = false, length = 100)
    private String provinceName;

    @Column(nullable = false)
    private Integer districtId;

    @Column(nullable = false, length = 100)
    private String districtName;

    @Column(nullable = false, length = 20)
    private String wardCode;

    @Column(nullable = false, length = 100)
    private String wardName;

    @Column(nullable = false)
    private String street;

    private String note;

    @Column(length = 500)
    private String fullAddress;

    @Column(precision = 10, scale = 7)
    private BigDecimal lat;

    @Column(precision = 10, scale = 7)
    private BigDecimal lng;
}
