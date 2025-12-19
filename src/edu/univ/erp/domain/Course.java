package edu.univ.erp.domain;

public class Course {
    private int courseId;
    private String courseName;
    private String instructorName;
    private int credits;

    public Course(int courseId, String courseName, String instructorName, int credits) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.instructorName = instructorName;
        this.credits = credits;
    }

    public int getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public String getInstructorName() { return instructorName; }
    public int getCredits() { return credits; }
}
