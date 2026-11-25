package com.samiksha.bank;

import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/** Interactive console front-end for {@link Bank}. */
public class BankApp {

    private final Bank bank;
    private final Scanner in;
    private final PrintStream out;

    public BankApp(Bank bank, InputStream input, PrintStream out) {
        this.bank = bank;
        this.in = new Scanner(input);
        this.out = out;
    }

    public static void main(String[] args) {
        new BankApp(new Bank(), System.in, System.out).run();
    }

    public void run() {
        out.println("=== Java Bank Account CLI ===");
        while (true) {
            printMenu();
            String choice = prompt("Choose an option");
            if (choice == null) {
                break;
            }
            try {
                switch (choice) {
                    case "1" -> createAccount();
                    case "2" -> deposit();
                    case "3" -> withdraw();
                    case "4" -> transfer();
                    case "5" -> showHistory();
                    case "6" -> listAccounts();
                    case "0", "q", "quit", "exit" -> {
                        out.println("Goodbye!");
                        return;
                    }
                    default -> out.println("Unknown option: " + choice);
                }
            } catch (BankException | IllegalArgumentException e) {
                out.println("Error: " + e.getMessage());
            }
        }
        out.println("Goodbye!");
    }

    private void printMenu() {
        out.println();
        out.println("1) Create account");
        out.println("2) Deposit");
        out.println("3) Withdraw");
        out.println("4) Transfer");
        out.println("5) Transaction history");
        out.println("6) List accounts");
        out.println("0) Exit");
    }

    private void createAccount() {
        String owner = prompt("Owner name");
        BigDecimal opening = readAmount("Opening deposit (0 for none)");
        if (owner == null || opening == null) {
            return;
        }
        Account account = opening.signum() == 0
                ? bank.createAccount(owner)
                : bank.createAccount(owner, opening);
        out.println("Created " + account.getAccountNumber() + " for " + account.getOwnerName()
                + " with balance " + account.getBalance());
    }

    private void deposit() {
        String number = prompt("Account number");
        BigDecimal amount = readAmount("Amount to deposit");
        if (number == null || amount == null) {
            return;
        }
        bank.deposit(number, amount);
        out.println("New balance: " + bank.getAccount(number).getBalance());
    }

    private void withdraw() {
        String number = prompt("Account number");
        BigDecimal amount = readAmount("Amount to withdraw");
        if (number == null || amount == null) {
            return;
        }
        bank.withdraw(number, amount);
        out.println("New balance: " + bank.getAccount(number).getBalance());
    }

    private void transfer() {
        String from = prompt("From account");
        String to = prompt("To account");
        BigDecimal amount = readAmount("Amount to transfer");
        if (from == null || to == null || amount == null) {
            return;
        }
        BigDecimal moved = bank.transfer(from, to, amount);
        out.println("Transferred " + moved + " from " + from.toUpperCase() + " to " + to.toUpperCase());
    }

    private void showHistory() {
        String number = prompt("Account number");
        if (number == null) {
            return;
        }
        Account account = bank.getAccount(number);
        out.println("History for " + account.getAccountNumber() + " (" + account.getOwnerName() + "):");
        account.getHistory().forEach(t -> out.println("  " + t));
        out.println("Current balance: " + account.getBalance());
    }

    private void listAccounts() {
        List<Account> accounts = bank.listAccounts();
        if (accounts.isEmpty()) {
            out.println("No accounts yet.");
            return;
        }
        accounts.forEach(a -> out.println("  " + a));
        out.println("Total held: " + bank.totalDeposits());
    }

    private String prompt(String label) {
        out.print(label + ": ");
        out.flush();
        if (!in.hasNextLine()) {
            return null;
        }
        return in.nextLine().strip();
    }

    private BigDecimal readAmount(String label) {
        String raw = prompt(label);
        if (raw == null) {
            return null;
        }
        try {
            return new BigDecimal(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not a valid amount: " + raw);
        }
    }
}
