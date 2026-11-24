package com.samiksha.bank;

/** Base type for all domain errors raised by the bank. */
public class BankException extends RuntimeException {
    public BankException(String message) {
        super(message);
    }
}
