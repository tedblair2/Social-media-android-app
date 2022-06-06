package com.example.newinstagram.Model;


public class Notification {
    private String userId;
    private String text;
    private String postid;
    private boolean isPost;

    public Notification() {
    }

    public Notification(String userId, String text, String postid, boolean isPost) {
        this.userId = userId;
        this.text = text;
        this.postid = postid;
        this.isPost = isPost;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }
}
