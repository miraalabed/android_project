package com.example.schoolhub;

public class ScheduleItem {
    private String day;
    private int periodNumber;
    private String subjectName;
    private String teacherName;
    private String startTime;
    private String endTime;

    public ScheduleItem(String day, int periodNumber, String subjectName, String teacherName, String startTime, String endTime) {
        this.day = day;
        this.periodNumber = periodNumber;
        this.subjectName = subjectName;
        this.teacherName = teacherName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDay() {
        return day;
    }

    public int getPeriodNumber() {
        return periodNumber;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
