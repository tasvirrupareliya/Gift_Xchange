package com.app.giftxchange.model;

public class Payment {

    private String paymentID;
    private String userID;
    private String otheruserID;

    public Payment(String paymentID,String userID, String otheruserID) {
        this.paymentID = paymentID;
        this.userID = userID;
        this.otheruserID = otheruserID;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getOtheruserID() {
        return otheruserID;
    }

    public void setOtheruserID(String otheruserID) {
        this.otheruserID = otheruserID;
    }
}
