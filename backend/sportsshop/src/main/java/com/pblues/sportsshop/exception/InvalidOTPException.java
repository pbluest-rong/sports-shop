package com.pblues.sportsshop.exception;

public class InvalidOTPException extends RuntimeException {
    public InvalidOTPException() {
        super("Invalid OTP");
    }
}
