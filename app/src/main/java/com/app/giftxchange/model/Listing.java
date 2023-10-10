package com.app.giftxchange.model;

import java.util.Date;

public class Listing {
    private String listID;
    private String userID;
    private String listTitle;
    private double listPrice;
    private String listDate;
    private String listLocation;
    private String listType;
    private boolean isBuy;
    private boolean isSell;

    public Listing(String userID, String listTitle, double listPrice, String listDate, String listLocation, String listType) {
        this.userID = userID;
        this.listTitle = listTitle;
        this.listPrice = listPrice;
        this.listDate = listDate;
        this.listLocation = listLocation;
        this.listType = listType;
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
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public double getListPrice() {
        return listPrice;
    }

    public void setListPrice(double listPrice) {
        this.listPrice = listPrice;
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
}
