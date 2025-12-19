package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String AUTH_URL = "jdbc:mysql://localhost:3306/auth_db";
    private static final String ERP_URL = "jdbc:mysql://localhost:3306/erp_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Appologaming11@";

    //  Connection to auth_db
    public static Connection getAuthConnection() {
        try {
            Connection conn = DriverManager.getConnection(AUTH_URL, USER, PASSWORD);
            System.out.println("? Connected to authdb");
            return conn;
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to authdb: " + e.getMessage());
            return null;
        }
    }

    //  Connection to erp_db
    public static Connection getErpConnection() {
        try {
            Connection conn = DriverManager.getConnection(ERP_URL, USER, PASSWORD);
            System.out.println("? Connected to erpdb");
            return conn;
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to erpdb: " + e.getMessage());
            return null;
        }
    }
}
