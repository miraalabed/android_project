package com.example.schoolhub;

public class Exam {
    private String subjectName;
    private String examType;
    private String examDate;
    private String description;

    public Exam(String subjectName, String examType, String examDate, String description) {
        this.subjectName = subjectName;
        this.examType = examType;
        this.examDate = examDate;
        this.description = description;
    }

    public String getSubjectName() { return subjectName; }
    public String getExamType() { return examType; }
    public String getExamDate() { return examDate; }
    public String getDescription() { return description; }
}
