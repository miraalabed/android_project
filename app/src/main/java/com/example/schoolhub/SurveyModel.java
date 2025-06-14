package com.example.schoolhub;

public class SurveyModel {
    private String id;
    private String title;
    private String createdAt;

    public SurveyModel(String id, String title, String createdAt) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
