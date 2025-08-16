package com.pblues.sportsshop.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String password;
    private String otp;
}