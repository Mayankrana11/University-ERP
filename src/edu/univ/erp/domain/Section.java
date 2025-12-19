package edu.univ.erp.domain;

public class Section {

    private int sectionId;
    private String courseCode;       // changed from int courseId
    private String instructorName;   // changed from instructorId
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String room;
    private int capacity;
    private String semester;
    private int year;

    // Constructor
    public Section(int sectionId, String courseCode, String instructorName,
                   String dayOfWeek, String startTime, String endTime,
                   String room, int capacity, String semester, int year) {

        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.instructorName = instructorName;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.capacity = capacity;
        this.semester = semester;
        this.year = year;
    }

    // Getters
    public int getSectionId() { return sectionId; }
    public String getCourseCode() { return courseCode; }
    public String getInstructorName() { return instructorName; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getRoom() { return room; }
    public int getCapacity() { return capacity; }
    public String getSemester() { return semester; }
    public int getYear() { return year; }
}
