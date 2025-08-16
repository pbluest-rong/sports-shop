package com.pblues.sportsshop.exception;

public class OTPExceededLimitException extends RuntimeException {
    public OTPExceededLimitException(String msg) {
        super(msg);
    }
}
