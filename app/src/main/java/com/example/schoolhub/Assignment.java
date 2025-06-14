package com.example.schoolhub;
public class Assignment {
    private int id;
    private String title;
    private String description;
    private String dueDate;

    public Assignment(int id, String title, String description, String dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDueDate() { return dueDate; }

}
