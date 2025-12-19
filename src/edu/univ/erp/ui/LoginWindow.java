package edu.univ.erp.ui;

import edu.univ.erp.service.AuthService;
import edu.univ.erp.auth.session.AppState;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginWindow() {
        setTitle("University ERP Login");
        setSize(400, 330);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("University ERP", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 70, 200));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        formPanel.setBackground(Color.WHITE);

        JLabel userLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        formPanel.add(userLabel);
        formPanel.add(usernameField);
        formPanel.add(passLabel);
        formPanel.add(passwordField);

        // Buttons section
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton loginButton = new JButton("Login");
        styleButton(loginButton, new Color(0, 100, 255), Color.WHITE);
        loginButton.addActionListener(e -> onLogin());

        JButton changePassBtn = new JButton("Change Password");
        styleButton(changePassBtn, new Color(255, 193, 7), Color.BLACK);
        changePassBtn.addActionListener(e -> onChangePassword());

        JButton exitButton = new JButton("Exit");
        styleButton(exitButton, new Color(120, 120, 120), Color.WHITE);
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(loginButton);
        buttonPanel.add(changePassBtn);
        buttonPanel.add(exitButton);

        // Layout
        panel.add(title, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void styleButton(JButton b, Color bg, Color fg) {
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(140, 35));
    }

    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter username and password!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //  Lockout check
        if (AuthService.isLocked(username)) {
            JOptionPane.showMessageDialog(this, "Account temporarily locked after 3 failed attempts.");
            return;
        }

        //  Validate login
        boolean isValid = AuthService.validateLogin(username, password);

        if (isValid) {
            //  If valid login, fetch role to open correct panel
            String role = AuthService.getUserRole(username);
            AppState.setCurrentUser(Map.of("username", username, "role", role));

            dispose(); // Close login window

            SwingUtilities.invokeLater(() -> {
                switch (role.toLowerCase()) {
                    case "admin" -> new AdminPanel(username).setVisible(true);
                    case "instructor" -> new InstructorPanel(username).setVisible(true);
                    case "student" -> new StudentPanel(username).setVisible(true);
                    default -> JOptionPane.showMessageDialog(null, "Unknown role: " + role);
                }
            });
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Change Password Handler
    private void onChangePassword() {
        String username = JOptionPane.showInputDialog(this, "Enter your username:");
        if (username == null || username.trim().isEmpty()) return;

        String oldPass = JOptionPane.showInputDialog(this, "Enter your current password:");
        if (oldPass == null) return;

        String newPass = JOptionPane.showInputDialog(this, "Enter your new password:");
        if (newPass == null || newPass.trim().isEmpty()) return;

        if (newPass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.");
            return;
        }

        boolean changed = AuthService.changePassword(username.trim(), oldPass.trim(), newPass.trim());
        if (changed) {
            JOptionPane.showMessageDialog(this, "Password changed successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials or unable to change password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //  Entry point for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true));
    }
}
