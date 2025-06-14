package com.example.schoolhub;

public class Survey {
    private int id;
    private String title;
    private String status;
    private String date;

    public Survey(int id, String title, String status, String date) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }
}
