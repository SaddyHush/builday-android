package com.agora.model;

/**
 * Created by asadz on 4/9/18.
 */

public class Notification {
    private String title;
    private String body;
    private int type;
    private String recieverID;
    private String eventID;
    private String secondUserID;
    private int statusCode;
    private String dateTime;

    public Notification(String title, String body, int type, String recieverID, String eventID, String secondUserID, int statusCode, String dateTime) {
        this.title = title;
        this.body = body;
        this.type = type;
        this.recieverID = recieverID;
        this.eventID = eventID;
        this.secondUserID = secondUserID;
        this.statusCode = statusCode;
        this.dateTime = dateTime;
    }

    public Notification() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRecieverID() {
        return recieverID;
    }

    public void setRecieverID(String recieverID) {
        this.recieverID = recieverID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getSecondUserID() {
        return secondUserID;
    }

    public void setSecondUserID(String secondUserID) {
        this.secondUserID = secondUserID;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
