package edu.univ.erp.ui.components;

import edu.univ.erp.data.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class SendNotificationWindow extends JFrame {

    public SendNotificationWindow() {
        setTitle("Send Notification");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Send Notification", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(0, 90, 200));

        JTextArea messageArea = new JTextArea();
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(messageArea);

        String[] roles = {"all", "student", "instructor", "admin"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        JButton sendBtn = new JButton("Send");
        sendBtn.setBackground(new Color(0, 120, 255));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        sendBtn.addActionListener(e -> {
            String msg = messageArea.getText().trim();
            String role = roleBox.getSelectedItem().toString();

            if (msg.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Message cannot be empty!");
                return;
            }

            try (Connection conn = DatabaseConnection.getErpConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO notifications (date, role, message) VALUES (?, ?, ?)")) {

                ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                ps.setString(2, role);
                ps.setString(3, msg);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Notification Sent Successfully!");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        panel.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(new JLabel("Message:"), BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        center.add(roleBox, BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);
        panel.add(sendBtn, BorderLayout.SOUTH);

        add(panel);
    }
}
