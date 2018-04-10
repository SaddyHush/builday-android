package com.agora.model;

/**
 * Created by asadz on 4/9/18.
 */

public class Notification {
    private String message;
    private String dateTime;
    private String secondUserID;

    public Notification(String message, String dateTime, String secondUserID) {
        this.message = message;
        this.dateTime = dateTime;
        this.secondUserID = secondUserID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSecondUserID() {
        return secondUserID;
    }

    public void setSecondUserID(String secondUserID) {
        this.secondUserID = secondUserID;
    }
}
