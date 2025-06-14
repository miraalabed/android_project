package com.example.schoolhub;

public class StudentModel {
    private int id;
    private String name;

    public StudentModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
