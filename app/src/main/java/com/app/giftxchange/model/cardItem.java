package com.app.giftxchange.model;

public class cardItem {
    private String name;
    private int logoResId;

    public cardItem(String name, int logoResId) {
        this.name = name;
        this.logoResId = logoResId;
    }

    public String getName() {
        return name;
    }

    public int getLogoResId() {
        return logoResId;
    }
}
