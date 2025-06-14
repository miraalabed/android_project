package com.example.schoolhub;
public class SurveyStudent {
    private final String name;
    private final String date;

    public SurveyStudent(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() { return name; }

    public String getDate() { return date; }
}
