package com.rev.test.transfers.exception;

public class DuplicateAccountException extends RuntimeException {
    public DuplicateAccountException() {
    }

    public DuplicateAccountException(String msg) {
        super(msg);
    }
}
