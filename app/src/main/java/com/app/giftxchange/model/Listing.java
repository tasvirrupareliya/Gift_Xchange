package com.app.giftxchange.model;

import java.util.Date;

public class Listing {
    private String listID;
    private String userID;
    private String listTitle;
    private double listPrice;
    private Date listDate;
    private String listLocation;
    private String listType;
    private boolean isBuy;
    private boolean isSell;

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

    public Date getListDate() {
        return listDate;
    }

    public void setListDate(Date listDate) {
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
