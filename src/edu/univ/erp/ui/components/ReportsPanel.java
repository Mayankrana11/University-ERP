package edu.univ.erp.ui.components;

import edu.univ.erp.data.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReportsPanel extends JPanel {
    private JPanel totalUsersLabel, totalStudentsLabel, totalInstructorsLabel, totalCoursesLabel;

    public ReportsPanel() {
        setLayout(new GridLayout(2, 2, 15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        totalUsersLabel = createReportCard("Total Users");
        totalStudentsLabel = createReportCard("Total Students");
        totalInstructorsLabel = createReportCard("Total Instructors");
        totalCoursesLabel = createReportCard("Total Courses");

        add(totalUsersLabel);
        add(totalStudentsLabel);
        add(totalInstructorsLabel);
        add(totalCoursesLabel);

        loadReportData();
    }

    private JPanel createReportCard(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 200), 2));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(0, 70, 150));

        JLabel valueLabel = new JLabel("0", SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(new Color(30, 30, 30));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void loadReportData() {
        try (Connection connAuth = DatabaseConnection.getAuthConnection();
             Connection connErp = DatabaseConnection.getErpConnection();
             Statement stmtAuth = connAuth.createStatement();
             Statement stmtErp = connErp.createStatement()) {

            // Total Users
            ResultSet rs = stmtAuth.executeQuery("SELECT COUNT(*) AS total FROM users_auth");
            if (rs.next())
                ((JLabel) totalUsersLabel.getComponent(1)).setText(String.valueOf(rs.getInt("total")));

            // Students
            rs = stmtAuth.executeQuery("SELECT COUNT(*) AS total FROM users_auth WHERE role='student'");
            if (rs.next())
                ((JLabel) totalStudentsLabel.getComponent(1)).setText(String.valueOf(rs.getInt("total")));

            // Instructors
            rs = stmtAuth.executeQuery("SELECT COUNT(*) AS total FROM users_auth WHERE role='instructor'");
            if (rs.next())
                ((JLabel) totalInstructorsLabel.getComponent(1)).setText(String.valueOf(rs.getInt("total")));

            // Courses → from erp_db 
            rs = stmtErp.executeQuery("SELECT COUNT(*) AS total FROM courses");
            if (rs.next())
                ((JLabel) totalCoursesLabel.getComponent(1)).setText(String.valueOf(rs.getInt("total")));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
