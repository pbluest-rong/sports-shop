package com.pblues.sportsshop.common.exception;

public class OTPExceededLimitException extends RuntimeException {
    public OTPExceededLimitException(String msg) {
        super(msg);
    }
}
