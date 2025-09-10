package com.pblues.sportsshop.common.exception;

public class InvalidOTPException extends RuntimeException {
    public InvalidOTPException() {
        super("Invalid OTP");
    }
}
