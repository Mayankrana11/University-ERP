package edu.univ.erp.service;

import edu.univ.erp.data.DatabaseConnection;
import edu.univ.erp.domain.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseService {

    //  Fetch all courses
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT course_id, course_name, instructor_name, credits FROM courses ORDER BY course_id ASC";

        try (Connection conn = DatabaseConnection.getErpConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("course_id");
                String name = rs.getString("course_name");
                String instructor = rs.getString("instructor_name"); // ✅ fixed column name
                int credits = rs.getInt("credits");
                courses.add(new Course(id, name, instructor, credits));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    //  Add a new course
    public boolean addCourse(String courseName, String instructor, int credits) {
        String query = "INSERT INTO courses (course_name, instructor_name, credits) VALUES (?, ?, ?)"; // ✅ fixed
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, courseName);
            stmt.setString(2, instructor);
            stmt.setInt(3, credits);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //  Edit existing course
    public boolean updateCourse(int id, String courseName, String instructor, int credits) {
        String query = "UPDATE courses SET course_name = ?, instructor_name = ?, credits = ? WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, courseName);
            stmt.setString(2, instructor);
            stmt.setInt(3, credits);
            stmt.setInt(4, id);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete course
    public boolean deleteCourse(int id) {
        String query = "DELETE FROM courses WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
