package com.samiksha.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** A single bank account holding a balance and its transaction history. */
public class Account {

    private final String accountNumber;
    private final String ownerName;
    private BigDecimal balance;
    private final List<Transaction> history = new ArrayList<>();

    Account(String accountNumber, String ownerName, BigDecimal openingBalance) {
        this.accountNumber = Objects.requireNonNull(accountNumber);
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException("Owner name must not be blank");
        }
        this.ownerName = ownerName.strip();
        BigDecimal opening = normalize(openingBalance == null ? BigDecimal.ZERO : openingBalance);
        if (opening.signum() < 0) {
            throw new InvalidAmountException(opening);
        }
        this.balance = opening;
        record(TransactionType.OPEN, opening, "Account opened");
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public void deposit(BigDecimal amount) {
        credit(amount, TransactionType.DEPOSIT, "Deposit");
    }

    public void withdraw(BigDecimal amount) {
        debit(amount, TransactionType.WITHDRAWAL, "Withdrawal");
    }

    void credit(BigDecimal amount, TransactionType type, String note) {
        BigDecimal value = requirePositive(amount);
        balance = balance.add(value);
        record(type, value, note);
    }

    void debit(BigDecimal amount, TransactionType type, String note) {
        BigDecimal value = requirePositive(amount);
        ensureFunds(value);
        balance = balance.subtract(value);
        record(type, value, note);
    }

    void ensureFunds(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(accountNumber, balance, normalize(amount));
        }
    }

    static BigDecimal requirePositive(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidAmountException(null);
        }
        BigDecimal value = normalize(amount);
        if (value.signum() <= 0) {
            throw new InvalidAmountException(value);
        }
        return value;
    }

    private static BigDecimal normalize(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    private void record(TransactionType type, BigDecimal amount, String note) {
        history.add(new Transaction(type, amount, balance, note, LocalDateTime.now()));
    }

    @Override
    public String toString() {
        return String.format("%s  %-20s %12s", accountNumber, ownerName, balance);
    }
}
