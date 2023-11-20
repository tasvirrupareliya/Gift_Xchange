package com.app.giftxchange.model;

public class Premium {
    private String premiumID;
    private String userID;

    public Premium(String premiumID, String userID) {
        this.premiumID = premiumID;
        this.userID = userID;
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
}
