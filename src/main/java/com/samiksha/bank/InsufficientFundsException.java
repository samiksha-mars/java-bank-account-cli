package com.samiksha.bank;

import java.math.BigDecimal;

/** Thrown when a withdrawal or transfer would overdraw an account. */
public class InsufficientFundsException extends BankException {
    public InsufficientFundsException(String accountNumber, BigDecimal balance, BigDecimal requested) {
        super("Insufficient funds in " + accountNumber + ": balance " + balance + ", requested " + requested);
    }
}
