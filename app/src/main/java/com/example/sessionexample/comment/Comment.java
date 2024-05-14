package com.example.sessionexample.comment;
// Comment.java
public class Comment {


    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    private String profileImage;
    private String userId;
    private String userName;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    private String postId;
    private String commentText;

    public Comment() {
        // Default constructor required for Firebase
    }

    public Comment(String userId, String userName, String commentText) {
        this.userId = userId;
        this.userName = userName;
        this.commentText = commentText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
// Add getters and setters
}
