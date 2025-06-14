package com.example.schoolhub;

public class Mark {
    private String subjectName;
    private String examName;
    private String mark;
    private String date;

    public Mark(String subjectName, String examName, String mark, String date) {
        this.subjectName = subjectName;
        this.examName = examName;
        this.mark = mark;
        this.date = date;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getExamName() {
        return examName;
    }

    public String getMark() {
        return mark;
    }

    public String getDate() {
        return date;
    }
}
