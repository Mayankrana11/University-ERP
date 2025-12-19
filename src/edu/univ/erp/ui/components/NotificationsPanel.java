package edu.univ.erp.ui.components;

import edu.univ.erp.data.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class NotificationsPanel extends JFrame {
    private final String role;

    public NotificationsPanel(String role) {
        this.role = role;
        setTitle("System Notifications | University ERP");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel heading = new JLabel("📢 System Notifications", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(new Color(20, 90, 200));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Date", "Message"}, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);

        // 🔄 Load notifications from DB
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT date, message FROM notifications WHERE role=? OR role='all' ORDER BY date DESC")) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getTimestamp("date"), rs.getString("message")});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading notifications: " + e.getMessage());
        }

        // Layout setup
        panel.add(heading, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);
    }
}
