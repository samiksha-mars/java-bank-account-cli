# Java Bank Account CLI

An interactive, in-memory console banking app written in plain Java. No database or
framework is required: accounts live in memory for the length of the session.

## Features

- Create accounts with an optional opening deposit (numbers are assigned as `ACC-1001`, `ACC-1002`, ...)
- Deposit, withdraw and transfer between accounts
- Per-account transaction history with timestamps and running balance
- List all accounts and the total held by the bank
- Money handled as `BigDecimal` rounded to 2 decimal places
- Custom exceptions for invalid input:
  - `InvalidAmountException`: zero or negative amounts
  - `InsufficientFundsException`: withdrawals or transfers that would overdraw an account
  - `AccountNotFoundException`: unknown account numbers
- Transfers are validated before any balance changes, so a failed transfer leaves both accounts untouched

## Tech Stack

- Java 21 (compiled with `--release 21`, runs on 21+)
- Maven (via the bundled wrapper)
- JUnit 5

## Usage

Compile, then run the main class from `target/classes`:

```bash
./mvnw -q compile
java -cp target/classes com.samiksha.bank.BankApp
```

### Menu

| Option | Action                  |
|--------|-------------------------|
| `1`    | Create account          |
| `2`    | Deposit                 |
| `3`    | Withdraw                |
| `4`    | Transfer                |
| `5`    | Transaction history     |
| `6`    | List accounts           |
| `0`    | Exit                    |

### Example

```text
Choose an option: 1
Owner name: Alice
Opening deposit (0 for none): 100
Created ACC-1001 for Alice with balance 100.00

Choose an option: 3
Account number: ACC-1001
Amount to withdraw: 500
Error: Insufficient funds in ACC-1001: balance 100.00, requested 500.00

Choose an option: 1
Owner name: Bob
Opening deposit (0 for none): 0
Created ACC-1002 for Bob with balance 0.00

Choose an option: 4
From account: ACC-1001
To account: ACC-1002
Amount to transfer: 30
Transferred 30 from ACC-1001 to ACC-1002

Choose an option: 5
Account number: ACC-1001
History for ACC-1001 (Alice):
  2026-11-24 18:37:21  OPEN             100.00  balance     100.00  Account opened
  2026-11-24 18:37:21  TRANSFER_OUT      30.00  balance      70.00  Transfer to ACC-1002
Current balance: 70.00
```

The app also accepts piped input, which is handy for scripted demos:

```bash
printf '1\nAlice\n100\n1\nBob\n0\n4\nACC-1001\nACC-1002\n30\n6\n0\n' \
  | java -cp target/classes com.samiksha.bank.BankApp
```

## Project Layout

```text
src/main/java/com/samiksha/bank/
  Bank.java          account registry, deposit/withdraw/transfer
  Account.java       balance + transaction history
  Transaction.java   immutable history entry
  BankApp.java       console menu (main class)
  *Exception.java    domain errors
src/test/java/com/samiksha/bank/
  AccountTest.java, BankTest.java, BankAppTest.java
```

## Build & Test

```bash
./mvnw test          # compile and run the JUnit 5 suite
./mvnw -q package    # build target/java-bank-account-cli-0.0.1-SNAPSHOT.jar
```
