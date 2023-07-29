package com.example.group3projectpetcare;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    private String username;
    private String location;
    private String number;
    private String email;
    private String password;
    private String profileImage; // Added field for profile image path

    public User() {
    }

    public User(String username, String location, String number, String email, String password, String profileImage) {
        this.username = username;
        this.location = location;
        this.number = number;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public String getLocation() {
        return location;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
