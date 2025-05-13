package ui.java;

import dao.java.AccountDAO;
import dao.java.CustomerDAO;
import model.java.Account;
import model.java.Customer;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();

        System.out.println(" Welcome to Bank Management System");
        System.out.println("1. New User");
        System.out.println("2. Existing User");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            // New user flow
            System.out.println(" Enter New Customer Details 🔹");

            System.out.print("Name: ");
            String name = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            System.out.print("Phone: ");
            String phone = scanner.nextLine();

            Customer newCustomer = new Customer(name, email, password, phone);
            boolean success = customerDAO.addCustomer(newCustomer);

            if (success) {
                System.out.println(" Customer added successfully.");
            } else {
                System.out.println(" Failed to add customer.");
                return;
            }

            Customer createdCustomer = customerDAO.getCustomerByEmail(email);
            if (createdCustomer != null) {
                System.out.print("Enter Account Type (Checking/Savings): ");
                String accountType = scanner.nextLine();

                System.out.print("Enter Initial Deposit: ");
                double balance = scanner.nextDouble();
                accountDAO.createAccount(createdCustomer.getCustomerId(), accountType, balance);

                System.out.println(" Account created successfully.");
            }

        } else if (choice == 2) {
            // Existing user flow
            System.out.print("Enter your email: ");
            String email = scanner.nextLine();

            Customer existingCustomer = customerDAO.getCustomerByEmail(email);
            if (existingCustomer == null) {
                System.out.println(" Customer not found.");
                return;
            }

            Account account = accountDAO.getAccount(existingCustomer.getCustomerId());
            if (account == null) {
                System.out.println(" Account not found.");
                return;
            }

            while (true) {
                System.out.println("\nWelcome, " + existingCustomer.getName());
                System.out.println("1. View Balance");
                System.out.println("2. Deposit Money");
                System.out.println("3. Withdraw Money");
                System.out.println("4. View Transaction History");
                System.out.println("5. Exit");
                System.out.print("Choose option: ");
                int opt = scanner.nextInt();

                switch (opt) {
                    case 1:
                        account = accountDAO.getAccount(existingCustomer.getCustomerId());
                        System.out.println("💰 Balance: " + account.getBalance());
                        break;

                    case 2:
                        System.out.print("Enter amount to deposit: ");
                        double deposit = scanner.nextDouble();
                        if (accountDAO.deposit(account.getAccountId(), deposit)) {
                            System.out.println(" Deposit successful.");
                        } else {
                            System.out.println(" Deposit failed.");
                        }
                        break;

                    case 3:
                        System.out.print("Enter amount to withdraw: ");
                        double withdraw = scanner.nextDouble();
                        if (accountDAO.withdraw(account.getAccountId(), withdraw)) {
                            System.out.println(" Withdrawal successful.");
                        } else {
                            System.out.println(" Withdrawal failed. (May be insufficient balance)");
                        }
                        break;

                    case 4:
                        List<String> history = accountDAO.getTransactionHistory(account.getAccountId());
                        System.out.println(" Transaction History:");
                        for (String t : history) {
                            System.out.println(t);
                        }
                        break;

                    case 5:
                        System.out.println(" Goodbye!");
                        scanner.close();
                        return;

                    default:
                        System.out.println(" Invalid option.");
                }
            }
        } else {
            System.out.println(" Invalid choice. Exiting.");
        }

        scanner.close();
    }
}
