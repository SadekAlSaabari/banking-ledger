# Banking Ledger

A simple Java command-line banking application backed by SQLite for persistent storage.

The application allows users to:

- Create bank accounts with a generated account ID
- Sign in using their account ID and name
- View their account balance
- Deposit funds
- Withdraw funds
- Transfer funds to another account
- View their transaction history

## Project Structure

- `UserCLI` — command-line interface and user interaction
- `BankingService` — core banking operations and business logic
- `Account` and `Transaction` — domain models
- `AccountDAO` and `AuditDAO` — data-access interfaces
- `jdbcAccountDAO` and `jdbcAuditDAO` — SQLite database implementations
- `DBConnectionManager` — database connection management
- `Myexceptions` — custom application exceptions
- `database.db` — local SQLite database file

## Requirements

- Java 25 or later
- Maven

The project uses the SQLite JDBC driver, which Maven downloads automatically.

## Running the Application

### Option 1: Run `UserCLI` directly

Run the `UserCLI` class from your IDE:

1. Open the project as a Maven project.
2. Ensure the project dependencies have been loaded.
3. Run:

```text
src/main/java/banking/UserCLI.java
```

Alternatively, run the `banking.UserCLI` class using your IDE's Java application runner.

### Option 2: Run with Maven

From the project root, run:

```bash
mvn compile exec:java -Dexec.mainClass=banking.UserCLI
```

The application will start in the terminal.

## Data Storage

The application stores account and transaction data in the local `database.db` SQLite database file. Keep this file in the project root when running the application.

## Notes

The database already contains 2 accounts that can be used to try the application:
1. james with an ID of A5I9N8, and a balance of £1100
2. andrew with an ID of N0T2A9, and a balance of £0
