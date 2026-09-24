package banking;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Scanner;

import banking.Myexceptions.AccountNotFoundException;
import banking.Myexceptions.InsufficientFundsException;
import banking.Myexceptions.NoTransactionsFoundException;

public class UserCLI {
    private final BankingService service;
    private boolean signedIn = false;

    public UserCLI() {
        service = new BankingService();
    }
    public static void main(String[] args) {
            UserCLI userCLI = new UserCLI();
            userCLI.start();
        }

    public void start() {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to the Bank!\n");

        OUTER:
        while (true) {
            System.out.println("Please choose an option:");
            System.out.println("1. Create Account");
            System.out.println("2. Sign in to Existing Account");
            System.out.println("3. Exit");
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    accountCreation(scanner);
                    break;
                case "2":
                    Account retrievedAccount = signIn(scanner);
                    if (signedIn && retrievedAccount != null) {
                        afterSignedInDialogue(scanner, retrievedAccount);
                    }
                    break;
                case "3":
                    System.out.println("Thank you for using the Bank. Goodbye!");
                    break OUTER;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }



    public void accountCreation(Scanner scanner) {
        System.out.println("Enter your name:");
        String newName = scanner.nextLine().toLowerCase();

        while (true) {
            System.out.println("Will you be making an initial deposit? (enter yes/no)\n");
            String depositChoice = scanner.nextLine().toLowerCase();

            if (depositChoice.equals("yes")) {
                System.out.println("Enter the initial deposit amount:");
                String initialDepositInput = scanner.nextLine();
                try {
                    BigDecimal initialBalance = new BigDecimal(initialDepositInput);
                    String newId = service.createAccount(newName, initialBalance);
                    System.out.println("Account created successfully! Your account ID is: " + newId + ". Don't forget to write it down somewhere safe!" + "\n");
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount. Please enter a valid number.");
                } catch (AccountNotFoundException | SQLException e) {
                    System.out.println("Error creating account: " + e.getMessage());
                }
            } else if (depositChoice.equals("no")) {
                try {
                    String newId = service.createAccount(newName, BigDecimal.ZERO);
                    System.out.println("Account created successfully! Your account ID is: " + newId + ". Don't forget to write it down somewhere safe!" + "\n");
                    break;
                } catch (AccountNotFoundException | SQLException e) {
                    System.out.println("Error creating account: " + e.getMessage());
                    break;
                }
            } else {
                System.out.println("Invalid choice. Please enter 'yes' or 'no'.");
            }
        }

    }

    public Account signIn(Scanner scanner) {
        System.out.println("Enter your name:");
        String name = scanner.nextLine().toLowerCase();
        System.out.println("Enter your account ID:");
        String id = scanner.nextLine().toUpperCase();

        try {
            Account account = service.retrieveAccount(id, name);
            System.out.println("Sign-in successful! Welcome back, " + account.getOwnerName() + "!");
            System.out.println("Your current balance is: £" + service.viewBalance(account) + "\n");
            signedIn = true;
            return account;
        } catch (AccountNotFoundException | SQLException e) {
            System.out.println("Error signing in: " + e.getMessage());
            return null;
        }
    }

    public void afterSignedInDialogue(Scanner scanner, Account account) {
        //provide options for the user after signing in, including making a deposit, withdrawing funds, viewing their transaction history, making a transfer, or signing out
        while (true) { 
            // Display options for the user after signing in
            System.out.println("What would you like to do?");
            System.out.println("1. Make a deposit");
            System.out.println("2. Withdraw funds");
            System.out.println("3. Make a transfer");
            System.out.println("4. View transaction history");
            System.out.println("5. Sign out");
            System.out.println("6. Quit application");

            String choice = scanner.nextLine();

            // Handle the user's choice
            switch (choice) {
                case "1":
                    // Handle deposit
                    makeADeposit(scanner, account);
                    break;
                case "2":
                    // Handle withdrawal
                    makeAWithdrawal(scanner, account);
                    break;
                case "3":
                    // Handle transfer
                    makeATransfer(scanner, account);
                    break;
                case "4":
                    // Handle viewing transaction history
                    viewTransactionHistory(scanner, account);
                    break;
                case "5":
                    signedIn = false;
                    System.out.println("You have been signed out. \n");
                    return; // Return to the main menu
                case "6":
                    System.out.println("Thank you for using the Bank. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Method to make a deposit
    public void makeADeposit(Scanner scanner, Account account) {
        System.out.println("Enter the amount to deposit:");
        String depositInput = scanner.nextLine();
        try {
            BigDecimal depositAmount = new BigDecimal(depositInput);
            service.deposit(account.getID(), account.getOwnerName(), depositAmount);
            System.out.println("Deposit successful! Your new balance is: £" + service.viewBalance(account) + "\n");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        } catch (AccountNotFoundException | SQLException e) {
            System.out.println("Error making deposit: " + e.getMessage());
        }
    }

    // Method to make a withdrawal
    public void makeAWithdrawal(Scanner scanner, Account account) {
        System.out.println("Enter the amount to withdraw:");
        String withdrawalInput = scanner.nextLine();
        try {
            BigDecimal withdrawalAmount = new BigDecimal(withdrawalInput);
            service.withdraw(account.getID(), account.getOwnerName(), withdrawalAmount);
            System.out.println("Withdrawal successful! Your new balance is: £" + service.viewBalance(account) + "\n");
        } catch (InsufficientFundsException e) {
            System.out.println("Insufficient funds. Your current balance is: £" + account.getBalance() + "\n");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        } catch (AccountNotFoundException | SQLException e) {
            System.out.println("Error making withdrawal: " + e.getMessage());
        }
    }

    // Method to make a transfer
    public void makeATransfer(Scanner scanner, Account account) {
        System.out.println("Enter the recipient's account ID:");
        String recipientId = scanner.nextLine().toUpperCase();
        System.out.println("Enter the amount to transfer:");
        String transferInput = scanner.nextLine();
        try {
            BigDecimal transferAmount = new BigDecimal(transferInput);
            service.transfer(account.getID(), account.getOwnerName(), recipientId, transferAmount);
            System.out.println("Transfer successful! Your new balance is: £" + account.getBalance() + "\n");
        } catch (InsufficientFundsException e) {
            System.out.println("Insufficient funds. Your current balance is: £" + account.getBalance() + "\n");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        } catch (AccountNotFoundException | SQLException e) {
            System.out.println("Error making transfer: " + e.getMessage());
        }
    }

    // Method to view transaction history
    public void viewTransactionHistory(Scanner scanner, Account account) {
        try {
            System.out.println(service.retrieveTransactionHistory(account.getID(), account.getOwnerName()));
        } catch (AccountNotFoundException | SQLException e) {
            System.out.println("Error retrieving transaction history: " + e.getMessage());
        } catch (NoTransactionsFoundException e) {
            System.out.println("No transactions found for your account.");
        }
    }
}
