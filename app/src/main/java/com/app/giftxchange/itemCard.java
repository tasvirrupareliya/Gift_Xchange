package com.app.giftxchange;

public class itemCard {
    private String cardName;
    private String brandName;
    private double price;

    public itemCard(String cardName, String brandName, double price) {
        this.cardName = cardName;
        this.brandName = brandName;
        this.price = price;
    }

    // Getter methods
    public String getCardName() {
        return cardName;
    }

    public String getBrandName() {
        return brandName;
    }

    public double getPrice() {
        return price;
    }
}
