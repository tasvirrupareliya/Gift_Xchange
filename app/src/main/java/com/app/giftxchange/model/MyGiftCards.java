package com.app.giftxchange.model;

public class MyGiftCards {

    String cardAmount,cardCVV,cardNumber,cardName,cardExpiryDate;

    public MyGiftCards(String cardAmount, String cardCVV, String cardNumber, String cardName, String cardExpiryDate) {
        this.cardAmount = cardAmount;
        this.cardCVV = cardCVV;
        this.cardNumber = cardNumber;
        this.cardName = cardName;
        this.cardExpiryDate = cardExpiryDate;
    }

    public String getCardAmount() {
        return cardAmount;
    }

    public void setCardAmount(String cardAmount) {
        this.cardAmount = cardAmount;
    }

    public String getCardCVV() {
        return cardCVV;
    }

    public void setCardCVV(String cardCVV) {
        this.cardCVV = cardCVV;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardExpiryDate() {
        return cardExpiryDate;
    }

    public void setCardExpiryDate(String cardExpiryDate) {
        this.cardExpiryDate = cardExpiryDate;
    }
}
