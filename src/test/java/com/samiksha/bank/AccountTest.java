package com.samiksha.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AccountTest {

    private static BigDecimal amt(String value) {
        return new BigDecimal(value);
    }

    @Test
    void newAccountRecordsOpeningTransaction() {
        Account account = new Account("ACC-1", "Alice", amt("50"));
        assertEquals(amt("50.00"), account.getBalance());
        assertEquals(1, account.getHistory().size());
        assertEquals(TransactionType.OPEN, account.getHistory().get(0).type());
    }

    @Test
    void depositIncreasesBalance() {
        Account account = new Account("ACC-1", "Alice", BigDecimal.ZERO);
        account.deposit(amt("25.50"));
        assertEquals(amt("25.50"), account.getBalance());
        assertEquals(TransactionType.DEPOSIT, account.getHistory().get(1).type());
    }

    @Test
    void withdrawDecreasesBalance() {
        Account account = new Account("ACC-1", "Alice", amt("100"));
        account.withdraw(amt("40"));
        assertEquals(amt("60.00"), account.getBalance());
    }

    @Test
    void withdrawingMoreThanBalanceIsRejected() {
        Account account = new Account("ACC-1", "Alice", amt("10"));
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(amt("10.01")));
        assertEquals(amt("10.00"), account.getBalance());
        assertEquals(1, account.getHistory().size());
    }

    @Test
    void withdrawingEntireBalanceIsAllowed() {
        Account account = new Account("ACC-1", "Alice", amt("10"));
        account.withdraw(amt("10"));
        assertEquals(amt("0.00"), account.getBalance());
    }

    @Test
    void negativeAndZeroAmountsAreRejected() {
        Account account = new Account("ACC-1", "Alice", amt("10"));
        assertThrows(InvalidAmountException.class, () -> account.deposit(amt("-5")));
        assertThrows(InvalidAmountException.class, () -> account.deposit(BigDecimal.ZERO));
        assertThrows(InvalidAmountException.class, () -> account.withdraw(amt("-1")));
        assertThrows(InvalidAmountException.class, () -> account.deposit(null));
    }

    @Test
    void negativeOpeningBalanceIsRejected() {
        assertThrows(InvalidAmountException.class, () -> new Account("ACC-1", "Alice", amt("-1")));
    }

    @Test
    void blankOwnerIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Account("ACC-1", "  ", BigDecimal.ZERO));
    }

    @Test
    void historyIsReadOnly() {
        Account account = new Account("ACC-1", "Alice", BigDecimal.ZERO);
        assertThrows(UnsupportedOperationException.class, () -> account.getHistory().clear());
    }
}
