import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String message) {
        super(message);
    }
}

class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}


class Account {
    private int accountId;
    private String name;
    private double balance;

    public Account(int accountId, String name, double balance) {
        this.accountId = accountId;
        this.name = name;
        this.balance = balance;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (balance - amount < 0)
            throw new InsufficientFundsException("Not enough funds");
        balance -= amount;
    }

    public List<Transaction> getTransactions(List<Transaction> allTransactions) {
        List<Transaction> accountTransactions = new ArrayList<>();
        for (Transaction transaction : allTransactions) {
            if (transaction.getOriginatingAccountId() == accountId || transaction.getResultingAccountId() == accountId)
                accountTransactions.add(transaction);
        }
        return accountTransactions;
    }
}

class Transaction {
    private double amount;
    private int originatingAccountId;
    private int resultingAccountId;
    private String transactionReason;

    public Transaction(double amount, int originatingAccountId, int resultingAccountId, String transactionReason) {
        this.amount = amount;
        this.originatingAccountId = originatingAccountId;
        this.resultingAccountId = resultingAccountId;
        this.transactionReason = transactionReason;
    }

    public double getAmount() {
        return amount;
    }

    public int getOriginatingAccountId() {
        return originatingAccountId;
    }

    public int getResultingAccountId() {
        return resultingAccountId;
    }

    public String getTransactionReason() {
        return transactionReason;
    }
}

class Bank {
    private String bankName;
    private List<Account> accounts;
    private List<Transaction> transactions;
    private double totalTransactionFeeAmount;
    private double totalTransferAmount;
    private double transactionFlatFeeAmount;
    private double transactionPercentFeeValue;

    public Bank(){

    }

    public Bank(String bankName, double transactionFlatFeeAmount, double transactionPercentFeeValue) {
        this.bankName = bankName;
        this.accounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.totalTransactionFeeAmount = 0;
        this.totalTransferAmount = 0;
        this.transactionFlatFeeAmount = transactionFlatFeeAmount;
        this.transactionPercentFeeValue = transactionPercentFeeValue;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void performTransaction(int originatingAccountId, int resultingAccountId, double amount, String transactionReason, boolean isFlatFee)
            throws AccountNotFoundException, InsufficientFundsException {
        Account originatingAccount = findAccountById(originatingAccountId);
        Account resultingAccount = findAccountById(resultingAccountId);

        if (originatingAccount == null || resultingAccount == null)
            throw new AccountNotFoundException("Account not found");

        double fee = isFlatFee ? transactionFlatFeeAmount : amount * transactionPercentFeeValue / 100;

        originatingAccount.withdraw(amount + fee);
        resultingAccount.deposit(amount);

        transactions.add(new Transaction(amount, originatingAccountId, resultingAccountId, transactionReason));
        totalTransactionFeeAmount += fee;
        totalTransferAmount += amount;
    }

    public double checkAccountBalance(int accountId) throws AccountNotFoundException {
        Account account = findAccountById(accountId);
        if (account == null)
            throw new AccountNotFoundException("Account not found");
        return account.getBalance();
    }

    public List<Account> getBankAccounts() {
        return accounts;
    }

    public double getTotalTransactionFeeAmount() {
        return totalTransactionFeeAmount;
    }

    public double getTotalTransferAmount() {
        return totalTransferAmount;
    }

    private Account findAccountById(int accountId) {
        for (Account account : accounts) {
            if (account.getAccountId() == accountId)
                return account;
        }
        return null;
    }

    public void depositMoney(int accountId, double amount) throws AccountNotFoundException {
        Account account = findAccountById(accountId);
        if (account == null)
            throw new AccountNotFoundException("Account not found");
        account.deposit(amount);
    }

    public void withdrawMoney(int accountId, double amount) throws AccountNotFoundException, InsufficientFundsException {
        Account account = findAccountById(accountId);
        if (account == null)
            throw new AccountNotFoundException("Account not found");
        account.withdraw(amount);
    }

    public List<Transaction> getTransactions(int accountId) throws AccountNotFoundException {
        Account account = findAccountById(accountId);
        if (account == null)
            throw new AccountNotFoundException("Account not found");
        return account.getTransactions(transactions);
    }


}




public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Bank bank = null;

        boolean exit = false;
        while (!exit) {
            System.out.println("\nWelcome to the Bank System!");
            System.out.println("1. Create Bank");
            System.out.println("2. Create Account");
            System.out.println("3. Perform Transaction");
            System.out.println("4. Deposit Money");
            System.out.println("5. Withdraw Money");
            System.out.println("6. List Transactions");
            System.out.println("7. Check Account Balance");
            System.out.println("8. List Bank Accounts");
            System.out.println("9. Check Bank Total Transaction Fee Amount");
            System.out.println("10. Check Bank Total Transfer Amount");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.println("\nCreate Bank:");
                    System.out.print("Enter Bank Name: ");
                    String bankName = scanner.nextLine();
                    System.out.print("Enter Transaction Flat Fee Amount: ");
                    double flatFeeAmount = scanner.nextDouble();
                    System.out.print("Enter Transaction Percent Fee Value: ");
                    double percentFeeValue = scanner.nextDouble();
                    bank = new Bank(bankName, flatFeeAmount, percentFeeValue);
                    System.out.println("Bank created successfully.");
                    break;
                case 2:
                    if (bank == null) {
                        System.out.println("Please create a bank first.");
                        break;
                    }
                    System.out.println("\nCreate Account:");
                    System.out.print("Enter Account ID: ");
                    int accountId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                    System.out.print("Enter Account Holder Name: ");
                    String accountName = scanner.nextLine();
                    System.out.print("Enter Initial Balance: ");
                    double initialBalance = scanner.nextDouble();
                    bank.addAccount(new Account(accountId, accountName, initialBalance));
                    System.out.println("Account created successfully.");
                    break;
                case 3:
                    if (bank == null) {
                        System.out.println("Please create a bank first.");
                        break;
                    }
                    System.out.println("\nPerform Transaction:");
                    System.out.print("Enter Originating Account ID: ");
                    int originatingAccountId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                    System.out.print("Enter Resulting Account ID: ");
                    int resultingAccountId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                    System.out.print("Enter Transaction Amount: ");
                    double amount = scanner.nextDouble();
                    scanner.nextLine(); // Consume the newline character
                    System.out.print("Enter Transaction Reason: ");
                    String transactionReason = scanner.nextLine();
                    System.out.print("Is it a flat fee transaction? (true/false): ");
                    boolean isFlatFee = scanner.nextBoolean();
                    try {
                        bank.performTransaction(originatingAccountId, resultingAccountId, amount, transactionReason, isFlatFee);
                        System.out.println("Transaction completed successfully.");
                    } catch (AccountNotFoundException | InsufficientFundsException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 4:
                    if (bank == null) {
                        System.out.println("Please create a bank first.");
                        break;
                    }
                    System.out.println("\nDeposit Money:");
                    System.out.print("Enter Account ID: ");
                    int depositAccountId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                    System.out.print("Enter Deposit Amount: ");
                    double depositAmount = scanner.nextDouble();
                    scanner.nextLine(); // Consume the newline character
                    try {
                        bank.depositMoney(depositAccountId, depositAmount);
                        System.out.println("Money deposited successfully.");
                    } catch (AccountNotFoundException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 5:
                    if (bank == null) {
                        System.out.println("Please create a bank first.");
                        break;
                    }
                    System.out.println("\nWithdraw Money:");
                    System.out.print("Enter Account ID: ");
                    int withdrawAccountId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                    System.out.print("Enter Withdrawal Amount: ");
                    double withdrawAmount = scanner.nextDouble();
                    scanner.nextLine(); // Consume the newline character
                    try {
                        bank.withdrawMoney(withdrawAccountId, withdrawAmount);
                        System.out.println("Money withdrawn successfully.");
                    } catch (AccountNotFoundException | InsufficientFundsException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 6:
                    if (bank == null) {
                        System.out.println("Please create a bank first.");
                        break;
                    }
                    System.out.println("\nList Transactions:");
                    System.out.print("Enter Account ID: ");
                    int transactionsAccountId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                    try {
                        List<Transaction> transactions = bank.getTransactions(transactionsAccountId);
                        for (Transaction transaction : transactions) {
                            System.out.println("Amount: $" + transaction.getAmount() + ", Originating Account ID: " + transaction.getOriginatingAccountId() + ", Resulting Account ID: " + transaction.getResultingAccountId() + ", Reason: " + transaction.getTransactionReason());
                        }
                    } catch (AccountNotFoundException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 7:
                    if (bank == null) {
                        System.out.println("Please create a bank first.");
                        break;
                    }
                    System.out.println("\nCheck Account Balance:");
                    System.out.print("Enter Account ID: ");
                    int balanceAccountId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                    try {
                        double balance = bank.checkAccountBalance(balanceAccountId);
                        System.out.println("Account Balance: $" + balance);
                    } catch (AccountNotFoundException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 8:
                    if (bank == null) {
                        System.out.println("Please create a bank first.");
                        break;
                    }
                    System.out.println("\nList Bank Accounts:");
                    List<Account> accounts = bank.getBankAccounts();
                    for (Account account : accounts) {
                        System.out.println("Account ID: " + account.getAccountId() + ", Name: " + account.getName() + ", Balance: $" + account.getBalance());
                    }
                    break;
                case 9:
                    if (bank == null) {
                        System.out.println("Please create a bank first.");
                        break;
                    }
                    System.out.println("\nCheck Bank Total Transaction Fee Amount:");
                    System.out.println("Total Transaction Fee Amount: $" + bank.getTotalTransactionFeeAmount());
                    break;
                case 10:
                    if (bank == null) {
                        System.out.println("Please create a bank first.");
                        break;
                    }
                    System.out.println("\nCheck Bank Total Transfer Amount:");
                    System.out.println("Total Transfer Amount: $" + bank.getTotalTransferAmount());
                    break;
                case 0:
                    exit = true;
                    System.out.println("Exiting the Bank System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }

        scanner.close();

    }
}