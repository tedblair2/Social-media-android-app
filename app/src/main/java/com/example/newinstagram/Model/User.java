package com.example.newinstagram.Model;

public class User {
    private String bio;
    private String name;
    private String username;
    private String imageurl;
    private String id;
    private String email;

    public User() {
    }

    public User(String bio, String name, String username, String imageurl, String id, String email) {
        this.bio = bio;
        this.name = name;
        this.username = username;
        this.imageurl = imageurl;
        this.id = id;
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
