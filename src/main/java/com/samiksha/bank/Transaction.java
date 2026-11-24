package com.samiksha.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** An immutable entry in an account's history. */
public record Transaction(
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String note,
        LocalDateTime timestamp) {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String toString() {
        return String.format("%s  %-12s %10s  balance %10s  %s",
                FORMAT.format(timestamp), type, amount, balanceAfter, note == null ? "" : note);
    }
}
