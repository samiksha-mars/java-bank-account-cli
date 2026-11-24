package com.samiksha.bank;

import java.math.BigDecimal;

/** Thrown when an amount is zero, negative or otherwise not usable. */
public class InvalidAmountException extends BankException {
    public InvalidAmountException(BigDecimal amount) {
        super("Amount must be greater than zero, but was " + amount);
    }
}
