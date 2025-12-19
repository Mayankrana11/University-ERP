package edu.univ.erp.service;

import edu.univ.erp.data.DatabaseConnection;
import java.sql.*;
import java.util.HashMap;

public class AuthService {
    private static final HashMap<String, Integer> loginAttempts = new HashMap<>();

    public static boolean validateLogin(String username, String password) {
        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT password, role FROM users WHERE username=?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String dbPass = rs.getString("password");
                if (dbPass.equals(password)) {
                    loginAttempts.put(username, 0);
                    return true;
                } else {
                    incrementAttempts(username);
                    return false;
                }
            } else return false;
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            return false;
        }
    }

    private static void incrementAttempts(String username) {
        int count = loginAttempts.getOrDefault(username, 0) + 1;
        loginAttempts.put(username, count);
        if (count >= 3)
            System.err.println("⚠ Account locked for user: " + username);
    }

    public static boolean isLocked(String username) {
        return loginAttempts.getOrDefault(username, 0) >= 3;
    }

    public static boolean changePassword(String username, String oldPass, String newPass) {
    String sql = "UPDATE users SET password=? WHERE username=? AND password=?";
    try (Connection conn = DatabaseConnection.getAuthConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, newPass);
        ps.setString(2, username);
        ps.setString(3, oldPass);

        int rows = ps.executeUpdate();
        return rows > 0; // returns true if password updated successfully
    } catch (Exception e) {
        System.err.println("Change password error: " + e.getMessage());
        return false;
    }
}



    public static String getUserRole(String username) {
    try (Connection conn = DatabaseConnection.getAuthConnection();
         PreparedStatement ps = conn.prepareStatement("SELECT role FROM users WHERE username=?")) {
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("role");
        }
    } catch (Exception e) {
        System.err.println("Error fetching role: " + e.getMessage());
    }
    return "unknown";
}

}
