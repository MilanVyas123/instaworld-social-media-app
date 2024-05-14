package com.example.sessionexample.ReelsPackage;

public class ReelsModel {

    private String videoUrl;
    private String title;
    private String desc;
    private String videoUrlId;
    private String videoUrlUserId;
    private String userPhotoUrl;
    private String currentUsername;

    private Boolean liked;

    public String increaseLike()
    {
        return String.valueOf(++this.likesCount);
    }

    public String decreaseLike()
    {
        return String.valueOf(--this.likesCount);
    }



    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    private int likesCount;

    public ReelsModel(String videoUrl, String title, String desc,String videoUrlId,String videoUrlUserId,String userPhotoUrl,String currentUsername) {
        this.videoUrl = videoUrl;
        this.title = title;
        this.desc = desc;
        this.videoUrlId = videoUrlId;
        this.videoUrlUserId = videoUrlUserId;
        this.userPhotoUrl = userPhotoUrl;
        this.currentUsername = currentUsername;

    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    public String getVideoUrlId() {
        return videoUrlId;
    }

    public void setVideoUrlId(String videoUrlId) {
        this.videoUrlId = videoUrlId;
    }

    public String getVideoUrlUserId() {
        return videoUrlUserId;
    }

    public void setVideoUrlUserId(String videoUrlUserId) {
        this.videoUrlUserId = videoUrlUserId;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }
}
