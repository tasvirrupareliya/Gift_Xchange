package com.app.giftxchange.model;

public class Premium {
    private String premiumID;
    private String userID;
    private String currentDate;

    public Premium(String premiumID, String userID, String currentDate) {
        this.premiumID = premiumID;
        this.userID = userID;
        this.currentDate = currentDate;
    }

    public String getPremiumID() {
        return premiumID;
    }

    public void setPremiumID(String premiumID) {
        this.premiumID = premiumID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
