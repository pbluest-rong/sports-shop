package com.pblues.sportsshop.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "otp", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class OTP {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private String otp;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @Column(nullable = false)
    private String email;
    private int requestCount = 0;
}
