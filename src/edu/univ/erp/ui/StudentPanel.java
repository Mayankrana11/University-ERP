package edu.univ.erp.ui;

import edu.univ.erp.data.DatabaseConnection;
import edu.univ.erp.service.MaintenanceService;
import edu.univ.erp.ui.components.NotificationsPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StudentPanel extends JFrame {
    private final String username;
    private final JTable coursesTable;
    private final DefaultTableModel coursesModel;

    public StudentPanel(String username) {
        super("Student Dashboard | University ERP");
        this.username = username;

        setSize(1000, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ---------------- Header 
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(13, 110, 253));
        header.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel title = new JLabel("Student Dashboard | University ERP", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(120, 120, 120));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutBtn.addActionListener(e -> logout());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setOpaque(false);
        JLabel welcome = new JLabel("Welcome, " + username);
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rightPanel.add(welcome);
        rightPanel.add(logoutBtn);

        header.add(title, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ---------------- Table 
        coursesModel = new DefaultTableModel(new Object[]{
                "Course ID", "Course Name", "Instructor", "Credits", "Capacity", "Drop Deadline"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        coursesTable = new JTable(coursesModel);
        coursesTable.setRowHeight(24);
        coursesTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(coursesTable);
        add(scrollPane, BorderLayout.CENTER);

        // ---------------- Buttons ----------------
        JButton enrollBtn = createButton("Enroll", new Color(0, 102, 204));
        JButton unenrollBtn = createButton("Unenroll", new Color(220, 53, 69));
        JButton myEnrollmentsBtn = createButton("My Enrollments", new Color(255, 193, 7), Color.BLACK);
        JButton timetableBtn = createButton("View Timetable", new Color(40, 167, 69));
        JButton gradesBtn = createButton("View Grades", new Color(102, 51, 153));
        JButton transcriptBtn = createButton("Download Transcript (CSV)", new Color(255, 193, 7), Color.BLACK);
        JButton notifBtn = createButton("Notifications", new Color(13, 110, 253));
        JButton refreshBtn = createButton("Refresh", new Color(40, 167, 69));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        buttonPanel.add(enrollBtn);
        buttonPanel.add(unenrollBtn);
        buttonPanel.add(myEnrollmentsBtn);
        buttonPanel.add(timetableBtn);
        buttonPanel.add(gradesBtn);
        buttonPanel.add(transcriptBtn);
        buttonPanel.add(notifBtn);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // ---------------- Button Actions ----------------
        enrollBtn.addActionListener(e -> enrollCourse());
        unenrollBtn.addActionListener(e -> unenrollCourse());
        myEnrollmentsBtn.addActionListener(e -> showEnrollments());
        timetableBtn.addActionListener(e -> showTimetable());
        gradesBtn.addActionListener(e -> showGrades());
        transcriptBtn.addActionListener(e -> downloadTranscript());
        notifBtn.addActionListener(e -> new NotificationsPanel("student").setVisible(true));
        refreshBtn.addActionListener(e -> loadCourses());

        // Load initial data
        loadCourses();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                loadCourses();
            }
        });
    }

    // ---------- Utility ----------
    private JButton createButton(String text, Color bg) {
        return createButton(text, bg, Color.WHITE);
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        return b;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginWindow().setVisible(true);
        }
    }

    // ---------- Maintenance Mode ----------
    private boolean isReadOnlyMode() {
        try {
            if (MaintenanceService.isMaintenanceOn()) {
                JOptionPane.showMessageDialog(this,
                        "⚠ System is currently in Maintenance Mode — actions are disabled.",
                        "Read-only Mode",
                        JOptionPane.WARNING_MESSAGE);
                return true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error checking maintenance mode: " + e.getMessage());
        }
        return false;
    }

    // ---------- Load All Courses ----------
    private void loadCourses() {
        coursesModel.setRowCount(0);
        String sql = "SELECT course_id, course_name, instructor_name, credits, capacity, " +
                "COALESCE(DATE_FORMAT(drop_deadline, '%Y-%m-%d'), '-') AS drop_deadline " +
                "FROM courses ORDER BY course_id";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                coursesModel.addRow(new Object[]{
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getString("instructor_name"),
                        rs.getInt("credits"),
                        rs.getInt("capacity"),
                        rs.getString("drop_deadline")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Enroll ----------
    private void enrollCourse() {
        if (isReadOnlyMode()) return;

        int row = coursesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course first!");
            return;
        }

        int courseId = (int) coursesModel.getValueAt(row, 0);
        String courseName = (String) coursesModel.getValueAt(row, 1);

        try (Connection conn = DatabaseConnection.getErpConnection()) {
            PreparedStatement capCheck = conn.prepareStatement("SELECT COUNT(*) FROM enrollments WHERE course_id = ?");
            capCheck.setInt(1, courseId);
            ResultSet capRs = capCheck.executeQuery();
            capRs.next();
            int enrolled = capRs.getInt(1);

            PreparedStatement capLimit = conn.prepareStatement(
                    "SELECT capacity, prerequisite_id FROM courses WHERE course_id = ?");
            capLimit.setInt(1, courseId);
            ResultSet crsRs = capLimit.executeQuery();
            crsRs.next();
            int capacity = crsRs.getInt("capacity");
            Integer prereq = crsRs.getInt("prerequisite_id");
            if (crsRs.wasNull()) prereq = null;

            if (enrolled >= capacity) {
                JOptionPane.showMessageDialog(this, "Course is full! Capacity: " + capacity);
                return;
            }

            if (prereq != null && prereq != 0) {
                PreparedStatement preqCheck = conn.prepareStatement(
                        "SELECT final_grade FROM grades_details WHERE student_name=? AND course_id=?");
                preqCheck.setString(1, username);
                preqCheck.setInt(2, prereq);
                ResultSet preqRs = preqCheck.executeQuery();
                if (!preqRs.next() || preqRs.getString("final_grade") == null) {
                    JOptionPane.showMessageDialog(this,
                            "You must complete the prerequisite course (ID: " + prereq + ") first!");
                    return;
                }
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO enrollments (student_name, course_id) VALUES (?, ?)");
            ps.setString(1, username);
            ps.setInt(2, courseId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Enrolled successfully in " + courseName);
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Already enrolled in this course!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // ---------- Unenroll ----------
    private void unenrollCourse() {
        if (isReadOnlyMode()) return;

        List<Integer> myCourses = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getErpConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT c.course_id, c.course_name, c.drop_deadline FROM enrollments e " +
                            "JOIN courses c ON e.course_id = c.course_id WHERE e.student_name=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            List<String> courseNames = new ArrayList<>();
            while (rs.next()) {
                myCourses.add(rs.getInt("course_id"));
                courseNames.add(rs.getString("course_name"));
            }
            if (courseNames.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No enrolled courses found.");
                return;
            }

            String selected = (String) JOptionPane.showInputDialog(this,
                    "Select a course to drop:", "Unenroll",
                    JOptionPane.PLAIN_MESSAGE, null,
                    courseNames.toArray(), courseNames.get(0));

            if (selected == null) return;

            int courseId = myCourses.get(courseNames.indexOf(selected));

            PreparedStatement check = conn.prepareStatement(
                    "SELECT drop_deadline FROM courses WHERE course_id=?");
            check.setInt(1, courseId);
            ResultSet dRs = check.executeQuery();
            if (dRs.next()) {
                Date deadline = dRs.getDate("drop_deadline");
                if (deadline != null && deadline.before(Date.valueOf(LocalDate.now()))) {
                    JOptionPane.showMessageDialog(this, "Cannot drop! Deadline has passed.");
                    return;
                }
            }

            PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM enrollments WHERE student_name=? AND course_id=?");
            del.setString(1, username);
            del.setInt(2, courseId);
            del.executeUpdate();

            JOptionPane.showMessageDialog(this, "Unenrolled successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error unenrolling: " + e.getMessage());
        }
    }

    // ---------- My Enrollments ----------
    private void showEnrollments() {
        String sql = "SELECT c.course_id, c.course_name, c.instructor_name, c.credits " +
                "FROM enrollments e JOIN courses c ON e.course_id = c.course_id WHERE e.student_name = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel tm = new DefaultTableModel(
                    new Object[]{"Course ID", "Course Name", "Instructor", "Credits"}, 0);
            while (rs.next()) {
                tm.addRow(new Object[]{
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getString("instructor_name"),
                        rs.getInt("credits")
                });
            }

            JTable table = new JTable(tm);
            JOptionPane.showMessageDialog(this, new JScrollPane(table),
                    "My Enrollments", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading enrollments: " + e.getMessage());
        }
    }

    // ---------- Timetable ----------

private void showTimetable() {

    String sql =
            "SELECT c.course_name, s.day_of_week, s.start_time, s.end_time, s.room " +
            "FROM enrollments e " +
            "JOIN courses c ON e.course_id = c.course_id " +
            "JOIN sections s ON c.course_code = s.course_code " +   // ✅ FIXED
            "WHERE e.student_name = ? " +
            "ORDER BY s.day_of_week, s.start_time";

    try (Connection conn = DatabaseConnection.getErpConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        DefaultTableModel tm = new DefaultTableModel(
                new Object[]{"Course", "Day", "Time", "Room"}, 0);
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

        while (rs.next()) {
            Time st = rs.getTime("start_time");
            Time et = rs.getTime("end_time");

            String time = "-";
            if (st != null && et != null) {
                time = st.toLocalTime().format(tf) + " - " + et.toLocalTime().format(tf);
            } else if (st != null) {
                time = st.toLocalTime().format(tf);
            }

            tm.addRow(new Object[]{
                    rs.getString("course_name"),
                    rs.getString("day_of_week"),
                    time,
                    rs.getString("room")
            });
        }

        JTable table = new JTable(tm);
        JOptionPane.showMessageDialog(this, new JScrollPane(table),
                "My Timetable", JOptionPane.PLAIN_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}

    // ---------- View Grades ----------
    private void showGrades() {
        String sql = "SELECT g.course_id, c.course_name, g.quiz, g.midterm, g.endsem, g.final_grade " +
                "FROM grades_details g JOIN courses c ON g.course_id=c.course_id " +
                "WHERE g.student_name=?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel tm = new DefaultTableModel(
                    new Object[]{"Course ID", "Course Name", "Quiz", "Midterm", "Endsem", "Final Grade"}, 0);
            while (rs.next()) {
                tm.addRow(new Object[]{
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getInt("quiz"),
                        rs.getInt("midterm"),
                        rs.getInt("endsem"),
                        rs.getString("final_grade")
                });
            }
            JTable table = new JTable(tm);
            JOptionPane.showMessageDialog(this, new JScrollPane(table),
                    "Grades", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading grades: " + e.getMessage());
        }
    }

    // ---------- Transcript Export ----------
    private void downloadTranscript() {
        String filename = "transcript_" + username + ".csv";
        String sql = "SELECT g.course_id, c.course_name, c.credits, g.quiz, g.midterm, g.endsem, g.final_grade " +
                "FROM grades_details g JOIN courses c ON g.course_id=c.course_id " +
                "WHERE g.student_name=?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            try (FileWriter fw = new FileWriter(filename)) {
                fw.write("Course ID,Course Name,Credits,Quiz,Midterm,Endsem,Final Grade\n");
                while (rs.next()) {
                    fw.write(rs.getInt("course_id") + "," +
                            rs.getString("course_name") + "," +
                            rs.getInt("credits") + "," +
                            rs.getInt("quiz") + "," +
                            rs.getInt("midterm") + "," +
                            rs.getInt("endsem") + "," +
                            rs.getString("final_grade") + "\n");
                }
            }
            JOptionPane.showMessageDialog(this, "Transcript saved as " + filename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving transcript: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentPanel("student1").setVisible(true));
    }
}
