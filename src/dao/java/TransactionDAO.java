package dao.java;

import util.java.DBConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionDAO {

    // Record a transaction (deposit/withdraw)
    public void recordTransaction(int accountId, double amount, String type) {
        String sql = "INSERT INTO transactions (account_id, amount, type) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            stmt.setDouble(2, amount);
            stmt.setString(3, type);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Error recording transaction: " + e.getMessage());
        }
    }
}
