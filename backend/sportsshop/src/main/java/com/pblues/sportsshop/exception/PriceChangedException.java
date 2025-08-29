package com.pblues.sportsshop.exception;

public class PriceChangedException extends RuntimeException {
    public PriceChangedException(String msg) {
        super(msg);
    }
}