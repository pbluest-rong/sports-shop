package com.pblues.sportsshop.common.exception;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String msg) {
        super(msg);
    }
}
