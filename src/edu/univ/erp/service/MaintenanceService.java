package edu.univ.erp.service;

import edu.univ.erp.data.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles system Maintenance Mode flag stored in the 'settings' table.
 * When Maintenance Mode is ON, students/instructors are read-only.
 */
public class MaintenanceService {

    private static final String KEY = "maintenance";

    /** Read the current maintenance flag. */
    public static boolean isMaintenanceOn() {
        String query = "SELECT `value` FROM `settings` WHERE `key_name` = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, KEY);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Boolean.parseBoolean(rs.getString("value"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading maintenance flag: " + e.getMessage());
        }
        return false; // default if not found or DB error
    }

    /** Set maintenance mode ON/OFF. */
    public static boolean setMaintenance(boolean on) {
        String value = String.valueOf(on);
        String query = "INSERT INTO `settings`(`key_name`,`value`) VALUES (?,?) "
                     + "ON DUPLICATE KEY UPDATE `value` = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, KEY);
            ps.setString(2, value);
            ps.setString(3, value);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating maintenance flag: " + e.getMessage());
            return false;
        }
    }
}
