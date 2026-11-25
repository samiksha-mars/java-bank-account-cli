package com.samiksha.bank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** In-memory registry of accounts and the operations that move money between them. */
public class Bank {

    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private int nextNumber = 1001;

    public Account createAccount(String ownerName, BigDecimal openingBalance) {
        String number = "ACC-" + nextNumber;
        Account account = new Account(number, ownerName, openingBalance);
        nextNumber++;
        accounts.put(number, account);
        return account;
    }

    public Account createAccount(String ownerName) {
        return createAccount(ownerName, BigDecimal.ZERO);
    }

    public Account getAccount(String accountNumber) {
        Account account = accountNumber == null ? null : accounts.get(accountNumber.strip().toUpperCase());
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }

    public List<Account> listAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public void deposit(String accountNumber, BigDecimal amount) {
        getAccount(accountNumber).deposit(amount);
    }

    public void withdraw(String accountNumber, BigDecimal amount) {
        getAccount(accountNumber).withdraw(amount);
    }

    /**
     * Moves money between two accounts. Validation happens before either balance changes,
     * so a failed transfer leaves both accounts untouched.
     *
     * @return the amount actually moved, rounded to 2 decimal places
     */
    public BigDecimal transfer(String fromNumber, String toNumber, BigDecimal amount) {
        Account from = getAccount(fromNumber);
        Account to = getAccount(toNumber);
        if (from == to) {
            throw new BankException("Cannot transfer to the same account");
        }
        BigDecimal value = Account.requirePositive(amount);
        from.ensureFunds(value);
        from.debit(value, TransactionType.TRANSFER_OUT, "Transfer to " + to.getAccountNumber());
        to.credit(value, TransactionType.TRANSFER_IN, "Transfer from " + from.getAccountNumber());
        return value;
    }

    public BigDecimal totalDeposits() {
        return accounts.values().stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
    }
}
