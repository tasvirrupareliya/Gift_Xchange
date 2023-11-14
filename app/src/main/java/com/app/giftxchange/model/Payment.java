package com.app.giftxchange.model;

public class Payment {

    private String paymentID;
    private String cardNo;
    private String cardExpiry;
    private String CVV;
    private String nameOnCard;
    private String userID;
    private String otheruserID;

    public Payment(String paymentID, String cardNo, String cardExpiry, String CVV, String nameOnCard, String userID, String otheruserID) {
        this.paymentID = paymentID;
        this.cardNo = cardNo;
        this.cardExpiry = cardExpiry;
        this.CVV = CVV;
        this.nameOnCard = nameOnCard;
        this.userID = userID;
        this.otheruserID = otheruserID;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardExpiry() {
        return cardExpiry;
    }

    public void setCardExpiry(String cardExpiry) {
        this.cardExpiry = cardExpiry;
    }

    public String getCVV() {
        return CVV;
    }

    public void setCVV(String CVV) {
        this.CVV = CVV;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
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
