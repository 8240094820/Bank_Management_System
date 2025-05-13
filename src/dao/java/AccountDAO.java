package dao.java;

import model.java.Account;
import util.java.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // Create a new account for a customer
    public boolean createAccount(int customerId, String accountType, double initialBalance) {
        String insertAccountQuery = "INSERT INTO accounts (customer_id, account_type, balance) VALUES (?, ?, ?)";
        String insertTransactionQuery = "INSERT INTO transactions (account_id, amount, type) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertAccountQuery, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, customerId);
            stmt.setString(2, accountType);
            stmt.setDouble(3, initialBalance);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int accountId = generatedKeys.getInt(1);

                    // Insert the initial deposit transaction
                    try (PreparedStatement transStmt = conn.prepareStatement(insertTransactionQuery)) {
                        transStmt.setInt(1, accountId);
                        transStmt.setDouble(2, initialBalance);
                        transStmt.setString(3, "deposit");
                        transStmt.executeUpdate();
                    }

                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Get account details for a customer
    public Account getAccount(int customerId) {
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Account(
                    rs.getInt("account_id"),
                    rs.getInt("customer_id"),
                    rs.getString("account_type"),
                    rs.getDouble("balance")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Error retrieving account: " + e.getMessage());
        }
        return null;
    }

    // Deposit money into an account
    public boolean deposit(int accountId, double amount) {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, amount);
            stmt.setInt(2, accountId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                recordTransaction(accountId, amount, "deposit");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error depositing money: " + e.getMessage());
        }
        return false;
    }

    // Withdraw money from an account
    public boolean withdraw(int accountId, double amount) {
        String checkBalanceQuery = "SELECT balance FROM accounts WHERE account_id = ?";
        String updateBalanceQuery = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkBalanceQuery)) {

            checkStmt.setInt(1, accountId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                if (currentBalance >= amount) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateBalanceQuery)) {
                        updateStmt.setDouble(1, amount);
                        updateStmt.setInt(2, accountId);
                        int rows = updateStmt.executeUpdate();
                        if (rows > 0) {
                            recordTransaction(accountId, amount, "withdraw");
                            return true;
                        }
                    }
                } else {
                    System.out.println("❌ Insufficient balance.");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error withdrawing money: " + e.getMessage());
        }
        return false;
    }

    // Record a transaction
    private void recordTransaction(int accountId, double amount, String type) {
        String sql = "INSERT INTO transactions (account_id, amount, type) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);
            stmt.setDouble(2, amount);
            stmt.setString(3, type);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ Error recording transaction: " + e.getMessage());
        }
    }

    // Get transaction history for an account
    public List<String> getTransactionHistory(int accountId) {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC";
        List<String> transactions = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String transaction = "ID: " + rs.getInt("transaction_id") +
                        ", Type: " + rs.getString("type") +
                        ", Amount: " + rs.getDouble("amount") +
                        ", Date: " + rs.getTimestamp("transaction_date");
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error retrieving transactions: " + e.getMessage());
        }
        return transactions;
    }
}
