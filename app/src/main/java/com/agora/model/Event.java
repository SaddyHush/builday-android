package com.agora.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by asadzeynal on 8/7/17.
 */

public class Event {
    private String ownerEmail;
    private ArrayList<String>  joinedUsers;
    private int usersLimit;
    private String createdAt;
    private float lat;
    private float lng;
    private String interest;
    private String title;

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public ArrayList<String> getJoinedUsers() {
        return joinedUsers;
    }

    public void setJoinedUsers(ArrayList<String> joinedUsers) {
        this.joinedUsers = joinedUsers;
    }

    public int getUsersLimit() {
        return usersLimit;
    }

    public void setUsersLimit(int usersLimit) {
        this.usersLimit = usersLimit;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }
}
