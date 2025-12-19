package edu.univ.erp.service;

import edu.univ.erp.data.DatabaseConnection;
import edu.univ.erp.domain.Section;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionService {

    public List<Section> getAllSections() {
        List<Section> list = new ArrayList<>();

        String sql = """
            SELECT s.section_id, s.course_code, c.course_name,
                   u.username AS instructor_name,
                   s.day_of_week, s.start_time, s.end_time,
                   s.room, s.capacity, s.semester, s.year
            FROM sections s
            JOIN courses c ON s.course_code = c.course_code
            JOIN users u ON s.instructor_id = u.id
            ORDER BY s.section_id
        """;

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Section(
                        rs.getInt("section_id"),
                        rs.getString("course_code"),
                        rs.getString("instructor_name"),
                        rs.getString("day_of_week"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getString("room"),
                        rs.getInt("capacity"),
                        rs.getString("semester"),
                        rs.getInt("year")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Helper: Get instructor ID by username
    public int getInstructorId(String username) throws Exception {
        String sql = "SELECT id FROM users WHERE username=? AND role='instructor'";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("id");

            throw new Exception("Instructor username not found: " + username);
        }
    }

    public boolean addSection(Section s, int instructorId) {
        String sql = """
            INSERT INTO sections
            (course_code, instructor_id, day_of_week, start_time,
             end_time, room, capacity, semester, year)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getCourseCode());
            ps.setInt(2, instructorId);
            ps.setString(3, s.getDayOfWeek());
            ps.setString(4, s.getStartTime());
            ps.setString(5, s.getEndTime());
            ps.setString(6, s.getRoom());
            ps.setInt(7, s.getCapacity());
            ps.setString(8, s.getSemester());
            ps.setInt(9, s.getYear());

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSection(int sectionId) {
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM sections WHERE section_id=?")) {

            ps.setInt(1, sectionId);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            return false;
        }
    }
}
