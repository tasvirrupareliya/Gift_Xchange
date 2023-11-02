package com.app.giftxchange.model;

import android.graphics.drawable.Drawable;

import java.util.Date;

public class Listing {
    private String userID;
    private String cardName;
    private String cardAmount;
    private String listDate;
    private String cardNumber;
    private String cardExpiryDate;
    private String cardCVV;
    private String listLocation;
    private String listType;
    private String listStatus;
    private String listID;
    private boolean isBuy;
    private boolean isSell;

    public Listing(String userID, String cardName, String cardAmount, String listDate, String listLocation, String listType, String cardNumber, String cardExpiryDate, String cardCVV, String listStatus, String listID) {
        this.userID = userID;
        this.cardName = cardName;
        this.cardAmount = cardAmount;
        this.listDate = listDate;
        this.listLocation = listLocation;
        this.listType = listType;
        this.listStatus = listStatus;
        this.listID = listID;
        this.cardNumber = cardNumber;
        this.cardCVV = cardCVV;
        this.cardExpiryDate = cardExpiryDate;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardAmount() {
        return cardAmount;
    }

    public void setCardAmount(String cardAmount) {
        this.cardAmount = cardAmount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpiryDate() {
        return cardExpiryDate;
    }

    public void setCardExpiryDate(String cardExpiryDate) {
        this.cardExpiryDate = cardExpiryDate;
    }

    public String getCardCVV() {
        return cardCVV;
    }

    public void setCardCVV(String cardCVV) {
        this.cardCVV = cardCVV;
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
