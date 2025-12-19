package edu.univ.erp.ui.components;

import edu.univ.erp.data.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageSectionsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public ManageSectionsPanel() {

        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("📅 Manage Sections / Timetable");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Table model
        model = new DefaultTableModel(new Object[]{
                "Section ID", "Course Code", "Course Name",
                "Instructor", "Day", "Start", "End",
                "Room", "Capacity", "Semester", "Year"
        }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addBtn = new JButton("➕ Add Section");
        JButton delBtn = new JButton("🗑️ Delete Section");
        JButton refBtn = new JButton("🔄 Refresh");

        addBtn.setBackground(new Color(40, 167, 69)); addBtn.setForeground(Color.WHITE);
        delBtn.setBackground(new Color(220, 53, 69)); delBtn.setForeground(Color.WHITE);
        refBtn.setBackground(new Color(25, 118, 210)); refBtn.setForeground(Color.WHITE);

        btns.add(addBtn);
        btns.add(delBtn);
        btns.add(refBtn);

        add(btns, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addSection());
        delBtn.addActionListener(e -> deleteSection());
        refBtn.addActionListener(e -> loadSections());

        loadSections();
    }


    // LOAD SECTIONS


    private void loadSections() {
        model.setRowCount(0);

        String sql = """
            SELECT s.section_id, s.course_code, c.course_name,
                   u.username AS instructor_name,
                   s.day_of_week, s.start_time, s.end_time,
                   s.room, s.capacity, s.semester, s.year
            FROM sections s
            JOIN courses c ON s.course_code = c.course_code
            JOIN auth_db.users u ON s.instructor_id = u.id
            ORDER BY s.section_id
        """;

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("section_id"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getString("instructor_name"),
                        rs.getString("day_of_week"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getString("room"),
                        rs.getInt("capacity"),
                        rs.getString("semester"),
                        rs.getInt("year")
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    // ADD SECTION


    private void addSection() {

        JTextField courseCode = new JTextField();
        JTextField instructorUser = new JTextField();
        JTextField day = new JTextField("Monday");
        JTextField start = new JTextField("09:00");
        JTextField end = new JTextField("10:00");
        JTextField room = new JTextField();
        JTextField capacity = new JTextField("30");
        JTextField sem = new JTextField("Fall");
        JTextField year = new JTextField("2025");

        Object[] form = {
                "Course Code:", courseCode,
                "Instructor Username:", instructorUser,
                "Day:", day,
                "Start (HH:mm):", start,
                "End (HH:mm):", end,
                "Room:", room,
                "Capacity:", capacity,
                "Semester:", sem,
                "Year:", year
        };

        int op = JOptionPane.showConfirmDialog(this, form, "Add Section", JOptionPane.OK_CANCEL_OPTION);
        if (op != JOptionPane.OK_OPTION) return;

        try (Connection connAuth = DatabaseConnection.getAuthConnection();
             PreparedStatement ps1 = connAuth.prepareStatement("SELECT id FROM users WHERE username=?")) {

            // fetch instructor ID
            ps1.setString(1, instructorUser.getText().trim());
            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Instructor not found!");
                return;
            }
            int instructorId = rs.getInt("id");


            // insert section
            String sql = """
                INSERT INTO sections (course_code, instructor_id, day_of_week,
                                      start_time, end_time, room, capacity,
                                      semester, year)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

            try (Connection conn = DatabaseConnection.getErpConnection();
                 PreparedStatement ps2 = conn.prepareStatement(sql)) {

                ps2.setString(1, courseCode.getText().trim());
                ps2.setInt(2, instructorId);
                ps2.setString(3, day.getText().trim());
                ps2.setString(4, start.getText().trim());
                ps2.setString(5, end.getText().trim());
                ps2.setString(6, room.getText().trim());
                ps2.setInt(7, Integer.parseInt(capacity.getText().trim()));
                ps2.setString(8, sem.getText().trim());
                ps2.setInt(9, Integer.parseInt(year.getText().trim()));

                ps2.executeUpdate();

                JOptionPane.showMessageDialog(this, "✔ Section Added");
                loadSections();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding section: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    // DELETE SECTION


    private void deleteSection() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a section first.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        int c = JOptionPane.showConfirmDialog(this, "Delete section #" + id + " ?", "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (c != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM sections WHERE section_id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "🗑 Deleted");
            loadSections();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
