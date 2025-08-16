package com.pblues.sportsshop.request;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String password;
    private String otp;
    private boolean acceptMarketing;
}