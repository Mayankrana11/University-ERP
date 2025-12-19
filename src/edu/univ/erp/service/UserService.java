package edu.univ.erp.service;

import edu.univ.erp.data.DatabaseConnection;
import edu.univ.erp.domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    //  Fetch all users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, username, role FROM users_auth";

        try (Connection conn = DatabaseConnection.getAuthConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    //  Add user
    public boolean addUser(String username, String password, String role) {
        String query = "INSERT INTO users_auth (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //  Update user
    public boolean updateUser(int id, String username, String role) {
        String query = "UPDATE users_auth SET username = ?, role = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, role);
            stmt.setInt(3, id);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //  Delete user
    public boolean deleteUser(int id) {
        String query = "DELETE FROM users_auth WHERE id = ?";

        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
