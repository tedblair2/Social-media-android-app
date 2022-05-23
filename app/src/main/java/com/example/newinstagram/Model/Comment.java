package com.example.newinstagram.Model;

public class Comment {
    private String commentId;
    private String Comment;
    private String Publisher;


    public Comment() {
    }

    public Comment(String commentId, String comment, String publisher) {
        this.commentId = commentId;
        Comment = comment;
        Publisher = publisher;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
    }
}
