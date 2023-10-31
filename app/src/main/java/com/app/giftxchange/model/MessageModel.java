package com.app.giftxchange.model;

public class MessageModel {
    String userId ;
    String userid2;
    String status;


    public MessageModel(String userId, String userid2,String status) {
        this.userId = userId;
        this.userid2 = userid2;
        this.status=status;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserid2() {
        return userid2;
    }

    public void setUserid2(String userid2) {
        this.userid2 = userid2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
