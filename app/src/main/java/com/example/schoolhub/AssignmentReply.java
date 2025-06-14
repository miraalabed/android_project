package com.example.schoolhub;

public class AssignmentReply {
    private String studentName;
    private String replyText;
    private String replyLink;

    public AssignmentReply(String studentName, String replyText, String replyLink) {
        this.studentName = studentName;
        this.replyText = replyText;
        this.replyLink = replyLink;
    }

    public String getStudentName() { return studentName; }
    public String getReplyText() { return replyText; }
    public String getReplyLink() { return replyLink; }
}
