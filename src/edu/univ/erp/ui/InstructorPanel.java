package edu.univ.erp.ui;

import edu.univ.erp.data.DatabaseConnection;
import edu.univ.erp.service.MaintenanceService;
import edu.univ.erp.ui.components.NotificationsPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.sql.*;

public class InstructorPanel extends JFrame {
    private final String instructorName;
    private final JTable coursesTable;
    private final DefaultTableModel model;

    public InstructorPanel(String instructorName) {
        super("Instructor Dashboard | University ERP");
        this.instructorName = instructorName;

        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Header 
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(13, 110, 253));
        header.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel title = new JLabel("Instructor Dashboard | University ERP", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JButton logoutBtn = createButton("Logout", new Color(220, 53, 69));
        logoutBtn.addActionListener(e -> logout());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setOpaque(false);
        JLabel welcome = new JLabel("Welcome, " + instructorName);
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rightPanel.add(welcome);
        rightPanel.add(logoutBtn);

        header.add(title, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center: Courses Table 
        model = new DefaultTableModel(new Object[]{"Course ID", "Course Name", "Credits"}, 0);
        coursesTable = new JTable(model);
        coursesTable.setRowHeight(24);
        coursesTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(new JScrollPane(coursesTable), BorderLayout.CENTER);

        // Footer: Actions 
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));

        JButton viewStudentsBtn = createButton("View Students", new Color(14, 116, 238));
        JButton enterMarksBtn = createButton("Enter Marks", new Color(255, 193, 7), Color.BLACK);
        JButton computeGradesBtn = createButton("Compute Final Grades", new Color(40, 167, 69));
        JButton viewStatsBtn = createButton("View Class Stats", new Color(102, 16, 242));
        JButton importBtn = createButton("Import CSV", new Color(0, 180, 180));
        JButton exportBtn = createButton("Export CSV", new Color(255, 140, 0));
        JButton notifBtn = createButton("Notifications", new Color(13, 110, 253));
        JButton refreshBtn = createButton("Refresh", new Color(0, 150, 0));

        btnPanel.add(viewStudentsBtn);
        btnPanel.add(enterMarksBtn);
        btnPanel.add(computeGradesBtn);
        btnPanel.add(viewStatsBtn);
        btnPanel.add(importBtn);
        btnPanel.add(exportBtn);
        btnPanel.add(notifBtn);
        btnPanel.add(refreshBtn);

        add(btnPanel, BorderLayout.SOUTH);

        //  Action Listeners 
        loadInstructorCourses();

        viewStudentsBtn.addActionListener(e -> showStudents());
        enterMarksBtn.addActionListener(e -> enterMarks());
        computeGradesBtn.addActionListener(e -> computeGrades());
        viewStatsBtn.addActionListener(e -> viewStats());
        importBtn.addActionListener(e -> importGradesCSV());
        exportBtn.addActionListener(e -> exportGradesCSV());
        refreshBtn.addActionListener(e -> loadInstructorCourses());
        notifBtn.addActionListener(e -> new NotificationsPanel("instructor").setVisible(true));
    }

    //  Utility 
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

    //  Load instructor’s courses 
    private void loadInstructorCourses() {
        model.setRowCount(0);
        String sql = "SELECT course_id, course_name, credits FROM courses WHERE instructor_name=?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, instructorName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getInt("credits")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage());
        }
    }

    //  Maintenance check helper 
    private boolean isReadOnlyMode() {
        try {
            if (MaintenanceService.isMaintenanceOn()) {
                JOptionPane.showMessageDialog(this,
                        "⚠ System is currently in Maintenance Mode — changes are disabled.",
                        "Read-only Mode",
                        JOptionPane.WARNING_MESSAGE);
                return true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error checking maintenance mode: " + e.getMessage());
        }
        return false;
    }

    // View Enrolled Students 
    private void showStudents() {
        int row = coursesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course first!");
            return;
        }
        int courseId = (int) model.getValueAt(row, 0);
        String sql = "SELECT e.student_name FROM enrollments e WHERE e.course_id=?";
        DefaultTableModel tm = new DefaultTableModel(new Object[]{"Student Name"}, 0);
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tm.addRow(new Object[]{rs.getString("student_name")});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        JTable table = new JTable(tm);
        JOptionPane.showMessageDialog(this, new JScrollPane(table), "Students Enrolled", JOptionPane.PLAIN_MESSAGE);
    }

    // Enter Marks 

    private void enterMarks() {
        if (isReadOnlyMode()) return;

        int row = coursesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course first!");
            return;
        }

        int courseId = (int) model.getValueAt(row, 0);
        String courseName = (String) model.getValueAt(row, 1);
        String student = JOptionPane.showInputDialog(this, "Enter student username:");
        if (student == null || student.isEmpty()) return;

        try {
            int quiz = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Quiz marks (out of 20):"));
            int mid = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Midterm marks (out of 30):"));
            int end = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter EndSem marks (out of 50):"));

            // Calculate Total and Grade Immediately ---
            int total = quiz + mid + end;
            String finalGrade = calculateGrade(total); 
           

            String sql = """
                INSERT INTO grades_details (student_name, course_id, quiz, midterm, endsem, final_grade)
                VALUES (?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE quiz=?, midterm=?, endsem=?, final_grade=?""";

            try (Connection conn = DatabaseConnection.getErpConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                // Insert values
                ps.setString(1, student);
                ps.setInt(2, courseId);
                ps.setInt(3, quiz);
                ps.setInt(4, mid);
                ps.setInt(5, end);
                ps.setString(6, finalGrade); // Save the calculated grade

                // Update values (if row exists)
                ps.setInt(7, quiz);
                ps.setInt(8, mid);
                ps.setInt(9, end);
                ps.setString(10, finalGrade); // Update the calculated grade

                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Marks saved for " + student + ". Final Grade: " + finalGrade);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number entered.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving marks: " + e.getMessage());
        }
    }
    // Helper to calculate letter grade based on total score
    private String calculateGrade(double total) {
        if (total >= 90) return "A";
        if (total >= 80) return "A-"; 
        if (total >= 70) return "B";
        if (total >= 60) return "C";
        if (total >= 50) return "D";
        return "F";
    }

    // ---------- Compute Final Grades 
   
    private void computeGrades() {
        if (isReadOnlyMode()) return;

        int row = coursesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course first!");
            return;
        }
        int courseId = (int) model.getValueAt(row, 0);

        String select = "SELECT student_name, quiz, midterm, endsem FROM grades_details WHERE course_id=?";
        String update = "UPDATE grades_details SET final_grade=? WHERE course_id=? AND student_name=?";
        
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement psSel = conn.prepareStatement(select);
             PreparedStatement psUpd = conn.prepareStatement(update)) {
            
            psSel.setInt(1, courseId);
            ResultSet rs = psSel.executeQuery();
            
            while (rs.next()) {
                int quiz = rs.getInt("quiz");
                int mid = rs.getInt("midterm");
                int end = rs.getInt("endsem");
                
                // Use the helper method
                String grade = calculateGrade(quiz + mid + end);
                
                psUpd.setString(1, grade);
                psUpd.setInt(2, courseId);
                psUpd.setString(3, rs.getString("student_name"));
                psUpd.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Final grades computed/refreshed successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error computing grades: " + e.getMessage());
        }
    }

    // ---------- View Class Statistics 
    private void viewStats() {
        int row = coursesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course first!");
            return;
        }
        int courseId = (int) model.getValueAt(row, 0);
        String sql = """
            SELECT AVG(quiz+midterm+endsem) AS avgMark,
                   MAX(quiz+midterm+endsem) AS maxMark,
                   MIN(quiz+midterm+endsem) AS minMark
            FROM grades_details WHERE course_id=?""";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this,
                        "Average Total: " + String.format("%.2f", rs.getDouble("avgMark")) +
                                "\nHighest Total: " + rs.getDouble("maxMark") +
                                "\nLowest Total: " + rs.getDouble("minMark"),
                        "Class Statistics", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No marks data found for this course.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching stats: " + e.getMessage());
        }
    }

    private void exportGradesCSV() {
        if (isReadOnlyMode()) return;

        int row = coursesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course first!");
            return;
        }
        int courseId = (int) model.getValueAt(row, 0);
        String filename = "grades_course_" + courseId + ".csv";
        String sql = "SELECT * FROM grades_details WHERE course_id=?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             FileWriter fw = new FileWriter(filename)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            fw.write("student_name,quiz,midterm,endsem,final_grade\n");
            while (rs.next()) {
                fw.write(rs.getString("student_name") + "," +
                        rs.getInt("quiz") + "," +
                        rs.getInt("midterm") + "," +
                        rs.getInt("endsem") + "," +
                        rs.getString("final_grade") + "\n");
            }
            JOptionPane.showMessageDialog(this, "Grades exported to " + filename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting: " + e.getMessage());
        }
    }

    // ---------- Import Grades 
    private void importGradesCSV() {
        if (isReadOnlyMode()) return;

        int row = coursesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course first!");
            return;
        }
        int courseId = (int) model.getValueAt(row, 0);

        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        try (BufferedReader br = new BufferedReader(new FileReader(file));
             Connection conn = DatabaseConnection.getErpConnection()) {
            String line;
            br.readLine(); // skip header
            String sql = """
                INSERT INTO grades_details (student_name, course_id, quiz, midterm, endsem, final_grade)
                VALUES (?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE quiz=?, midterm=?, endsem=?, final_grade=?""";
            PreparedStatement ps = conn.prepareStatement(sql);
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 6) continue;
                ps.setString(1, data[0]);
                ps.setInt(2, courseId);
                ps.setInt(3, Integer.parseInt(data[1]));
                ps.setInt(4, Integer.parseInt(data[2]));
                ps.setInt(5, Integer.parseInt(data[3]));
                ps.setString(6, data[4]);
                ps.setInt(7, Integer.parseInt(data[1]));
                ps.setInt(8, Integer.parseInt(data[2]));
                ps.setInt(9, Integer.parseInt(data[3]));
                ps.setString(10, data[4]);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Grades imported successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error importing: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InstructorPanel("Instructor1").setVisible(true));
    }
}
