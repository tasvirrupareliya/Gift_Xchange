package com.app.giftxchange.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String title;
    private String price;
    private String location;
    private String date;
    private String userEmail;

    private String username;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;

    public UserModel() {
    }

    public UserModel(String title, String price, String location, String date, String userEmail, String username, Timestamp createdTimestamp, String userId) {
        this.title = title;
        this.price = price;
        this.location = location;
        this.date = date;
        this.userEmail = userEmail;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
