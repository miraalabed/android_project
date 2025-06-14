package com.example.schoolhub;

public class Message {
    int id;
    String title;
    String content;
    String senderType;
    String sentAt;
    int senderId;

    public Message(int id, String title, String content, String senderType, String sentAt, int senderId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.senderType = senderType;
        this.sentAt = sentAt;
        this.senderId = senderId;
    }
}
