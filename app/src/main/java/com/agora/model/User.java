package com.agora.model;


import java.util.ArrayList;

public class User {

    private String name;
    private String status;
    private String surname;
    private String email;
    private String gender;
    private String password;
    private String created_at;
    private String newPassword;
    private String workPlace;
    private String yourInterests;
    private String yourInfo;
    private ArrayList<String> upcomingEvents;
    private ArrayList<String> appliedEvents;
    private String mainPhoto;
    private String mainPhotoSmall;

    public User() {
    }

    public ArrayList<String> getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(ArrayList<String> upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }

    public ArrayList<String> getAppliedEvents() {
        return appliedEvents;
    }

    public void setAppliedEvents(ArrayList<String> appliedEvents) {
        this.appliedEvents = appliedEvents;
    }

    public User(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMainPhotoSmall() {
        return mainPhotoSmall;
    }

    public void setMainPhotoSmall(String mainPhotoSmall) {
        this.mainPhotoSmall = mainPhotoSmall;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public String getYourInterests() {
        return yourInterests;
    }

    public void setYourInterests(String yourInterests) {
        this.yourInterests = yourInterests;
    }

    public String getYourInfo() {
        return yourInfo;
    }

    public void setYourInfo(String yourInfo) {
        this.yourInfo = yourInfo;
    }

    private String token;

    public String getMainPhoto() {
        return  mainPhoto;
    }

    public void setMainPhoto(String mainPhoto) {
        this. mainPhoto = mainPhoto;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
