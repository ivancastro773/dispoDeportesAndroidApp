package com.example.dispo_deportessr.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class WaitList {

    @SerializedName("id")
    private int id;

    @SerializedName("id_court")
    private int id_court;

    @SerializedName("id_users")
    private int id_users;

    @SerializedName("id_place")
    private int id_place;

    @SerializedName("date")
    private Date date;

    @SerializedName("courtNumber")
    private int courtNumber;

    @SerializedName("entryTime")
    private String entryTime;

    @SerializedName("departureTime")
    private String departureTime;

    @SerializedName("name")
    private String name;

    @SerializedName("tokenIdPhone")
    private String tokenIdPhone;

    public int getId_place() {
        return id_place;
    }

    public void setId_place(int id_place) {
        this.id_place = id_place;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCourtNumber() {
        return courtNumber;
    }

    public void setCourtNumber(int courtNumber) {
        this.courtNumber = courtNumber;
    }

    public String getTokenIdPhone() {
        return tokenIdPhone;
    }

    public void setTokenIdPhone(String tokenIdPhone) {
        this.tokenIdPhone = tokenIdPhone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_court() {
        return id_court;
    }

    public void setId_court(int id_court) {
        this.id_court = id_court;
    }

    public int getId_users() {
        return id_users;
    }

    public void setId_users(int id_users) {
        this.id_users = id_users;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
