package com.hsbc.gltc.globalkalendar.bean;

import android.graphics.Bitmap;
import java.security.Timestamp;
import java.util.List;

/**
 * Created by Henyue-GZ on 2014/7/9.
 */
public class BaseComment {
    private String commentId;
    private String userId;
    private String userName;
    private String content;
    private List<Bitmap> imageList;
    private Timestamp postTime;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Bitmap> getImageList() {
        return imageList;
    }

    public void setImageList(List<Bitmap> imageList) {
        this.imageList = imageList;
    }

    public Timestamp getPostTime() {
        return postTime;
    }

    public void setPostTime(Timestamp postTime) {
        this.postTime = postTime;
    }
}
