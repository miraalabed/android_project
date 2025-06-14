package com.example.schoolhub;

public class AssignmentModel {
    private int id;
    private String title;

    public AssignmentModel(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
