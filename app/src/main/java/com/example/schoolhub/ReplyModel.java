package com.example.schoolhub;

public class ReplyModel {
    private String studentName;
    private String replyContent;
    private String replyLink;
    private String assignmentTitle;
    private String createdAt;

    public ReplyModel(String studentName, String replyContent, String replyLink, String assignmentTitle, String createdAt) {
        this.studentName = studentName;
        this.replyContent = replyContent;
        this.replyLink = replyLink;
        this.assignmentTitle = assignmentTitle;
        this.createdAt = createdAt;
    }

    public String getStudentName() { return studentName; }
    public String getReplyContent() { return replyContent; }
    public String getReplyLink() { return replyLink; }
    public String getAssignmentTitle() { return assignmentTitle; }
    public String getCreatedAt() { return createdAt; }
}
