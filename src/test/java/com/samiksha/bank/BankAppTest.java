package com.samiksha.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class BankAppTest {

    private String run(Bank bank, String input) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        new BankApp(bank, new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), out).run();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void scriptedSessionCreatesDepositsAndTransfers() {
        Bank bank = new Bank();
        String output = run(bank, String.join("\n",
                "1", "Alice", "100",
                "1", "Bob", "0",
                "4", "ACC-1001", "ACC-1002", "25",
                "3", "ACC-1002", "500",
                "0", ""));

        assertEquals(new BigDecimal("75.00"), bank.getAccount("ACC-1001").getBalance());
        assertEquals(new BigDecimal("25.00"), bank.getAccount("ACC-1002").getBalance());
        assertTrue(output.contains("Created ACC-1001"));
        assertTrue(output.contains("Error: Insufficient funds"));
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    void transferConfirmationShowsTheRoundedAmountMoved() {
        Bank bank = new Bank();
        String output = run(bank, String.join("\n",
                "1", "Alice", "100",
                "1", "Bob", "0",
                "4", "ACC-1001", "ACC-1002", "10.005",
                "4", "ACC-1001", "ACC-1002", "1e1",
                "0", ""));

        assertEquals(2, output.split("Transferred 10.00 from ACC-1001 to ACC-1002", -1).length - 1, output);
        assertFalse(output.contains("Transferred 10.005"), output);
        assertFalse(output.contains("Transferred 1E+1"), output);
        assertEquals(new BigDecimal("20.00"), bank.getAccount("ACC-1002").getBalance());
    }

    @Test
    void invalidAmountInputIsReportedNotThrown() {
        String output = run(new Bank(), "1\nAlice\nabc\n0\n");
        assertTrue(output.contains("Error: Not a valid amount: abc"));
    }

    @Test
    void endOfInputExitsCleanly() {
        String output = run(new Bank(), "");
        assertTrue(output.contains("Goodbye!"));
    }
}
