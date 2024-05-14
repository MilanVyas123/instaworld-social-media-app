package com.example.sessionexample;

public class PostFeed {
    private String postId;
    private String imageURL;
    private String caption;

    private String postUserId;

    public String getLikesCount() {
        return likesCount+" likes";
    }


    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String increaseLike()
    {
        return ++this.likesCount+" likes";
    }

    public String decreaseLike()
    {
        return --this.likesCount+" likes";
    }



    private int likesCount;

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    private Boolean liked;

    public String getPostUserId() {
        return postUserId;
    }

    public void setPostUsername(String postUsername) {
        this.postUserId = postUsername;
    }

    private String userName;

    private String profileImage;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public PostFeed() {
    }

    public PostFeed(String postId, String imageURL, String caption, String userName, String profileImage) {
        this.postId = postId;
        this.imageURL = imageURL;
        this.caption = caption;
        this.userName = userName;
        this.profileImage = profileImage;
    }
}