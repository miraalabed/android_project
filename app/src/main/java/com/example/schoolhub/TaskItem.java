package com.example.schoolhub;

public class TaskItem {
    private String title;
    private int iconRes;

    public TaskItem(String title, int iconRes) {
        this.title = title;
        this.iconRes = iconRes;
    }

    public String getTitle() {
        return title;
    }

    public int getIconRes() {
        return iconRes;
    }
}
