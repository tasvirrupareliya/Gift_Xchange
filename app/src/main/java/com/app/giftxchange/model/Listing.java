package com.app.giftxchange.model;

import android.graphics.drawable.Drawable;

import java.util.Date;

public class Listing {
    private String userID;
    private String cardName;
    private String cardAmount;
    private String listDate;
    private String listLocation;
    private String listType;
    private String listStatus;
    private String listID;
    private boolean isBuy;
    private boolean isSell;

    public Listing(String userID, String cardName, String cardAmount, String listDate, String listLocation, String listType, String listStatus, String listID) {
        this.userID = userID;
        this.cardName = cardName;
        this.cardAmount = cardAmount;
        this.listDate = listDate;
        this.listLocation = listLocation;
        this.listType = listType;
        this.listStatus = listStatus;
        this.listID = listID;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getListTitle() {
        return cardName;
    }

    public void setListTitle(String cardName) {
        this.cardName = cardName;
    }

    public String getListPrice() {
        return cardAmount;
    }

    public void setListPrice(String cardAmount) {
        this.cardAmount = cardAmount;
    }

    public String getListDate() {
        return listDate;
    }

    public void setListDate(String listDate) {
        this.listDate = listDate;
    }

    public String getListLocation() {
        return listLocation;
    }

    public void setListLocation(String listLocation) {
        this.listLocation = listLocation;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

    public String getListStatus() {
        return listStatus;
    }

    public void setListStatus(String listStatus) {
        this.listStatus = listStatus;
    }
}
