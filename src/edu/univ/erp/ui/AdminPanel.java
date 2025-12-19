package edu.univ.erp.ui;

import edu.univ.erp.data.DatabaseConnection;
import edu.univ.erp.service.MaintenanceService;
import edu.univ.erp.ui.components.ManageUsersPanel;
import edu.univ.erp.ui.components.ManageCoursesPanel;
import edu.univ.erp.ui.components.ManageSectionsPanel;
import edu.univ.erp.ui.components.ReportsPanel;
import edu.univ.erp.ui.components.SendNotificationWindow;
import edu.univ.erp.ui.components.NotificationsPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class AdminPanel extends JFrame {

    private final String username;
    private JCheckBox maintenanceToggle;
    private JLabel totalUsersLabel;
    private JLabel totalCoursesLabel;
    private JLabel totalEnrollmentsLabel;
    private JPanel maintenanceBanner;
    private ChartPanel barChartPanel;
    private ChartPanel pieChartPanel;

    public AdminPanel(String username) {
        super("Admin Dashboard | University ERP");
        this.username = username;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                loadDashboard();
            }
        });

        loadDashboard();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(11, 100, 235));
        header.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel title = new JLabel("University ERP | Admin Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JLabel welcome = new JLabel("Welcome, " + username);
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(240, 80, 80));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setPreferredSize(new Dimension(90, 30));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });

        right.add(welcome);
        right.add(logoutBtn);

        maintenanceBanner = new JPanel();
        maintenanceBanner.setBackground(new Color(255, 230, 80));
        maintenanceBanner.setVisible(false);
        JLabel bannerText = new JLabel("⚠ System is in Maintenance Mode — Read-only access.");
        bannerText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bannerText.setForeground(Color.DARK_GRAY);
        maintenanceBanner.add(bannerText);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(maintenanceBanner, BorderLayout.SOUTH);

        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return wrapper;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 12, 12));
        totalUsersLabel = createStatLabel();
        totalCoursesLabel = createStatLabel();
        totalEnrollmentsLabel = createStatLabel();

        statsRow.add(buildStatCard("Users", totalUsersLabel));
        statsRow.add(buildStatCard("Courses", totalCoursesLabel));
        statsRow.add(buildStatCard("Enrollments", totalEnrollmentsLabel));

        JPanel charts = new JPanel(new GridLayout(1, 2, 12, 12));
        barChartPanel = new ChartPanel(createBarChart(new DefaultCategoryDataset()));
        pieChartPanel = new ChartPanel(createPieChart(new DefaultPieDataset()));

        charts.add(barChartPanel);
        charts.add(pieChartPanel);

        center.add(statsRow, BorderLayout.NORTH);
        center.add(charts, BorderLayout.CENTER);

        return center;
    }

    private JLabel createStatLabel() {
        JLabel lbl = new JLabel("0", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbl.setForeground(new Color(12, 109, 240));
        return lbl;
    }

    private JPanel buildStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 255), 1),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel name = new JLabel(title, SwingConstants.CENTER);
        name.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        name.setForeground(Color.DARK_GRAY);

        card.add(name, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(new EmptyBorder(10, 12, 12, 12));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));

        JButton addUserBtn   = createFooterButton("Add User", new Color(14, 116, 238));
        JButton addCourseBtn = createFooterButton("Add Course", new Color(40, 167, 69));
        JButton sectionsBtn  = createFooterButton("Manage Sections", new Color(32, 201, 151));
        JButton reportsBtn   = createFooterButton("Reports & Backup", new Color(255, 193, 7));
        JButton notifBtn     = createFooterButton("Notifications", new Color(13, 110, 253));
        JButton sendNotifBtn = createFooterButton("Send Notification", new Color(0, 123, 255)); // NEW BUTTON
        JButton refreshBtn   = createFooterButton("Refresh", new Color(100, 100, 100));

        addUserBtn.addActionListener(e -> openManageUsers());
        addCourseBtn.addActionListener(e -> openManageCourses());
        sectionsBtn.addActionListener(e -> openManageSections());
        reportsBtn.addActionListener(e -> openReports());
        notifBtn.addActionListener(e -> new NotificationsPanel("admin").setVisible(true));
        sendNotifBtn.addActionListener(e -> new SendNotificationWindow().setVisible(true));  // NEW ACTION
        refreshBtn.addActionListener(e -> loadDashboard());

        actions.add(addUserBtn);
        actions.add(addCourseBtn);
        actions.add(sectionsBtn);
        actions.add(reportsBtn);
        actions.add(notifBtn);
        actions.add(sendNotifBtn);   
        actions.add(refreshBtn);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        right.setOpaque(false);

        maintenanceToggle = new JCheckBox("Enable Maintenance Mode");
        maintenanceToggle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        maintenanceToggle.addActionListener(e -> toggleMaintenance(maintenanceToggle.isSelected()));

        right.add(maintenanceToggle);

        footer.add(actions, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);

        return footer;
    }

    private JButton createFooterButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(220, 36));
        return b;
    }

    private void openManageUsers() {
        JFrame f = new JFrame("Manage Users");
        f.setSize(900, 600);
        f.setLocationRelativeTo(this);
        f.add(new ManageUsersPanel());
        f.setVisible(true);
    }

    private void openManageCourses() {
        JFrame f = new JFrame("Manage Courses");
        f.setSize(900, 600);
        f.setLocationRelativeTo(this);
        f.add(new ManageCoursesPanel());
        f.setVisible(true);
    }

    private void openManageSections() {
        JFrame f = new JFrame("Manage Sections / Timetable");
        f.setSize(1000, 600);
        f.setLocationRelativeTo(this);
        f.add(new ManageSectionsPanel());
        f.setVisible(true);
    }

    private void openReports() {
        JFrame f = new JFrame("Reports & Backup");
        f.setSize(900, 600);
        f.setLocationRelativeTo(this);
        f.add(new ReportsPanel());
        f.setVisible(true);
    }

    private void loadDashboard() {
        SwingUtilities.invokeLater(() -> {
            int users = 0, courses = 0, enrollments = 0;
            int studentCount = 0, instrCount = 0, adminCount = 0;

            DefaultCategoryDataset barDs = new DefaultCategoryDataset();

            try (Connection conn = DatabaseConnection.getAuthConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT role, COUNT(*) AS c FROM users GROUP BY role")) {

                while (rs.next()) {
                    String role = rs.getString("role");
                    int c = rs.getInt("c");
                    users += c;

                    switch (role.toLowerCase()) {
                        case "student" -> studentCount += c;
                        case "instructor" -> instrCount += c;
                        case "admin" -> adminCount += c;
                    }
                }
            } catch (Exception ex) {
                showError("Error loading users: " + ex.getMessage());
            }

            try (Connection conn = DatabaseConnection.getErpConnection();
                 Statement st = conn.createStatement()) {

                ResultSet rs1 = st.executeQuery("SELECT COUNT(*) AS c FROM courses");
                if (rs1.next()) courses = rs1.getInt("c");

                String q = """
                        SELECT c.course_name,
                               COUNT(e.student_name) AS enrolled
                        FROM courses c
                        LEFT JOIN enrollments e
                          ON c.course_id = e.course_id
                        GROUP BY c.course_id, c.course_name
                        """;

                ResultSet rs2 = st.executeQuery(q);
                while (rs2.next()) {
                    barDs.addValue(rs2.getInt("enrolled"), "Students",
                            rs2.getString("course_name"));
                }

                ResultSet rs3 = st.executeQuery("SELECT COUNT(*) AS c FROM enrollments");
                if (rs3.next()) enrollments = rs3.getInt("c");

            } catch (Exception ex) {
                showError("Error loading courses/enrollments: " + ex.getMessage());
            }

            totalUsersLabel.setText(String.valueOf(users));
            totalCoursesLabel.setText(String.valueOf(courses));
            totalEnrollmentsLabel.setText(String.valueOf(enrollments));

            DefaultPieDataset pieDs = new DefaultPieDataset();
            pieDs.setValue("Students", studentCount);
            pieDs.setValue("Instructors", instrCount);
            pieDs.setValue("Admins", adminCount);

            barChartPanel.setChart(createBarChart(barDs));
            pieChartPanel.setChart(createPieChart(pieDs));

            try {
                boolean maintenance = MaintenanceService.isMaintenanceOn();
                maintenanceToggle.setSelected(maintenance);
                maintenanceBanner.setVisible(maintenance);
            } catch (Exception ignored) {}
        });
    }

    private JFreeChart createBarChart(DefaultCategoryDataset ds) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Courses vs Enrollments",
                "Course",
                "Students",
                ds,
                PlotOrientation.VERTICAL,
                false, true, false
        );
        CategoryPlot p = chart.getCategoryPlot();
        p.setBackgroundPaint(Color.WHITE);
        p.setRangeGridlinePaint(Color.GRAY);
        return chart;
    }

    private JFreeChart createPieChart(DefaultPieDataset ds) {
        JFreeChart chart = ChartFactory.createPieChart(
                "User Roles Distribution",
                ds,
                true, true, false
        );
        PiePlot p = (PiePlot) chart.getPlot();
        p.setSimpleLabels(true);
        return chart;
    }

    private void toggleMaintenance(boolean enable) {
        try {
            MaintenanceService.setMaintenance(enable);
            maintenanceBanner.setVisible(enable);
        } catch (Exception ex) {
            showError("Failed to toggle maintenance: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
