package edu.univ.erp.ui.components;

import edu.univ.erp.domain.Course;
import edu.univ.erp.service.CourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageCoursesPanel extends JPanel {
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private CourseService courseService;

    public ManageCoursesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        courseService = new CourseService();

        JLabel title = new JLabel("📘 Manage Courses");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(25, 118, 210));
        title.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));

        // Table setup
        tableModel = new DefaultTableModel(new Object[]{"Course ID", "Course Name", "Instructor", "Credits"}, 0);
        courseTable = new JTable(tableModel);
        courseTable.setRowHeight(25);
        courseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        JButton addBtn = new JButton("➕ Add Course");
        JButton editBtn = new JButton("✏️ Edit Course");
        JButton deleteBtn = new JButton("🗑️ Delete Course");
        JButton refreshBtn = new JButton("🔄 Refresh");

        styleButton(addBtn, new Color(25, 118, 210));
        styleButton(editBtn, new Color(255, 167, 38));
        styleButton(deleteBtn, new Color(244, 67, 54));
        styleButton(refreshBtn, new Color(56, 142, 60));

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadCourses();

        // Button actions
        addBtn.addActionListener(e -> addCourse());
        editBtn.addActionListener(e -> editCourse());
        deleteBtn.addActionListener(e -> deleteCourse());
        refreshBtn.addActionListener(e -> loadCourses());
    }

    private void styleButton(JButton button, Color bg) {
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void loadCourses() {
        tableModel.setRowCount(0);
        List<Course> courses = courseService.getAllCourses();
        for (Course c : courses) {
            tableModel.addRow(new Object[]{
                c.getCourseId(),
                c.getCourseName(),
                c.getInstructorName(),
                c.getCredits()
            });
        }
    }

    private void addCourse() {
        JTextField nameField = new JTextField();
        JTextField instructorField = new JTextField();
        JTextField creditsField = new JTextField();

        Object[] fields = {
            "Course Name:", nameField,
            "Instructor Name:", instructorField,
            "Credits:", creditsField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add Course", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String instructor = instructorField.getText().trim();
            String creditsText = creditsField.getText().trim();

            if (name.isEmpty() || instructor.isEmpty() || creditsText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Please fill all fields!");
                return;
            }

            try {
                int credits = Integer.parseInt(creditsText);
                if (courseService.addCourse(name, instructor, credits)) {
                    JOptionPane.showMessageDialog(this, "✅ Course added successfully!");
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed to add course.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "⚠️ Credits must be a number!");
            }
        }
    }

    private void editCourse() {
        int row = courseTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a course to edit.");
            return;
        }

        int courseId = (int) tableModel.getValueAt(row, 0);
        String currentName = (String) tableModel.getValueAt(row, 1);
        String currentInstructor = (String) tableModel.getValueAt(row, 2);
        int currentCredits = (int) tableModel.getValueAt(row, 3);

        JTextField nameField = new JTextField(currentName);
        JTextField instructorField = new JTextField(currentInstructor);
        JTextField creditsField = new JTextField(String.valueOf(currentCredits));

        Object[] fields = {
            "Course Name:", nameField,
            "Instructor Name:", instructorField,
            "Credits:", creditsField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Edit Course", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            String newInstructor = instructorField.getText().trim();
            try {
                int newCredits = Integer.parseInt(creditsField.getText().trim());
                if (courseService.updateCourse(courseId, newName, newInstructor, newCredits)) {
                    JOptionPane.showMessageDialog(this, "✅ Updated successfully!");
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed to update course.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "⚠️ Credits must be numeric!");
            }
        }
    }

    private void deleteCourse() {
        int row = courseTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a course to delete.");
            return;
        }

        int courseId = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this course?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (courseService.deleteCourse(courseId)) {
                JOptionPane.showMessageDialog(this, "🗑️ Course deleted successfully!");
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to delete course.");
            }
        }
    }
}
