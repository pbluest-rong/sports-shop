package com.pblues.sportsshop.common.exception;

public class PriceChangedException extends RuntimeException {
    public PriceChangedException(String msg) {
        super(msg);
    }
}