package com.agora.model;

import java.util.ArrayList;

/**
 * Created by asadzeynal on 8/7/17.
 */

public class Event {
    private String _id;
    private String ownerID;
    private ArrayList<String> acceptedUserID;
    private ArrayList<String> appliedUserID;
    private double usersLimit;
    private String created_at;
    private double lat;
    private double lng;
    private String interest;
    private String title;
    private String eventDateTime;


    public ArrayList<String> getAppliedUserID() {
        return appliedUserID;
    }

    public void setAppliedUserID(ArrayList<String> appliedUserID) {
        this.appliedUserID = appliedUserID;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public ArrayList<String> getAcceptedUserID() {
        return acceptedUserID;
    }

    public void setAcceptedUserID(ArrayList<String> acceptedUserID) {
        this.acceptedUserID = acceptedUserID;
    }

    public double getUsersLimit() {
        return usersLimit;
    }

    public void setUsersLimit(double usersLimit) {
        this.usersLimit = usersLimit;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }
}
