package com.example.schoolhub;
public class Resource {
    private String title, description, file_link, created_at;

    public Resource(String title, String description, String file_link, String created_at) {
        this.title = title;
        this.description = description;
        this.file_link = file_link;
        this.created_at = created_at;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getFileLink() { return file_link; }
    public String getCreatedAt() { return created_at; }
}
