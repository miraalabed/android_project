package com.example.schoolhub;

public class PreparationPlan {
    String subject, title, content, date;

    public PreparationPlan(String subject, String title, String content, String date) {
        this.subject = subject;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public String getSubject() { return subject; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getDate() { return date; }
}
