package com.example.sessionexample;

import android.net.Uri;

public class User {

    String name,email,password;
    String profileImage = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png";
    String status="public";
    String bio = "";

    public User(String name,String email,String bio,String profileImage)
    {
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.profileImage = profileImage;
        this.status = status;
    }

    public User() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return  bio; }

    public void setBio(String bio) { this.bio = bio; }

    public String getProfileImage() { return profileImage; }

    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public String getStatus() { return  status; }
    public void setStatus(String status) { this.status = status; }

}
