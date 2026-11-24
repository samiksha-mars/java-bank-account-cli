package com.samiksha.bank;

/** Thrown when an account number does not exist in the bank. */
public class AccountNotFoundException extends BankException {
    public AccountNotFoundException(String accountNumber) {
        super("No account found with number " + accountNumber);
    }
}
