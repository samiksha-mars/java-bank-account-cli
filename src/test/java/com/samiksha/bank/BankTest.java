package com.samiksha.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BankTest {

    private Bank bank;
    private Account alice;
    private Account bob;

    private static BigDecimal amt(String value) {
        return new BigDecimal(value);
    }

    @BeforeEach
    void setUp() {
        bank = new Bank();
        alice = bank.createAccount("Alice", amt("100"));
        bob = bank.createAccount("Bob");
    }

    @Test
    void createsAccountsWithUniqueNumbers() {
        assertNotEquals(alice.getAccountNumber(), bob.getAccountNumber());
        assertEquals(2, bank.listAccounts().size());
        assertEquals(amt("0.00"), bob.getBalance());
    }

    @Test
    void lookupIsCaseInsensitive() {
        assertEquals(alice, bank.getAccount(alice.getAccountNumber().toLowerCase()));
    }

    @Test
    void unknownAccountThrows() {
        assertThrows(AccountNotFoundException.class, () -> bank.getAccount("ACC-9999"));
        assertThrows(AccountNotFoundException.class, () -> bank.deposit("nope", amt("1")));
    }

    @Test
    void depositAndWithdrawThroughBank() {
        bank.deposit(bob.getAccountNumber(), amt("30"));
        bank.withdraw(bob.getAccountNumber(), amt("12.25"));
        assertEquals(amt("17.75"), bob.getBalance());
    }

    @Test
    void transferMovesMoneyAndRecordsBothSides() {
        assertEquals(amt("40.00"), bank.transfer(alice.getAccountNumber(), bob.getAccountNumber(), amt("40")));

        assertEquals(amt("60.00"), alice.getBalance());
        assertEquals(amt("40.00"), bob.getBalance());

        List<Transaction> aliceHistory = alice.getHistory();
        List<Transaction> bobHistory = bob.getHistory();
        assertEquals(TransactionType.TRANSFER_OUT, aliceHistory.get(aliceHistory.size() - 1).type());
        assertEquals(TransactionType.TRANSFER_IN, bobHistory.get(bobHistory.size() - 1).type());
    }

    @Test
    void overdraftTransferLeavesBothAccountsUnchanged() {
        assertThrows(InsufficientFundsException.class,
                () -> bank.transfer(bob.getAccountNumber(), alice.getAccountNumber(), amt("1")));
        assertEquals(amt("0.00"), bob.getBalance());
        assertEquals(amt("100.00"), alice.getBalance());
        assertEquals(1, alice.getHistory().size());
        assertEquals(1, bob.getHistory().size());
    }

    @Test
    void negativeTransferIsRejected() {
        assertThrows(InvalidAmountException.class,
                () -> bank.transfer(alice.getAccountNumber(), bob.getAccountNumber(), amt("-10")));
    }

    @Test
    void transferToSameAccountIsRejected() {
        assertThrows(BankException.class,
                () -> bank.transfer(alice.getAccountNumber(), alice.getAccountNumber(), amt("10")));
    }

    @Test
    void transferToUnknownAccountDoesNotDebitSender() {
        assertThrows(AccountNotFoundException.class,
                () -> bank.transfer(alice.getAccountNumber(), "ACC-9999", amt("10")));
        assertEquals(amt("100.00"), alice.getBalance());
    }

    @Test
    void totalDepositsSumsAllBalances() {
        bank.deposit(bob.getAccountNumber(), amt("25"));
        assertEquals(amt("125.00"), bank.totalDeposits());
    }
}
