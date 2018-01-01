package com.example.sukrit.blogapp;

/**
 * Created by Sukrit on 6/24/2017.
 */

public class Blog {

    private String title;
    private String desc;
    private String Image;
    private String username;
    private Long likesCount;

    public Blog() {
    }

    public Blog(String title, String desc, String image, String username,Long likesCount) {
        this.title = title;
        this.desc = desc;
        this.Image = image;
        this.username=username;
        this.likesCount=likesCount;
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

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getLikesCount() {
        return likesCount;
    }

    public void setLikes(Long likes) {
        this.likesCount = likes;
    }
}
