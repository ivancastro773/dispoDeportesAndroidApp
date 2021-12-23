package com.example.dispo_deportessr.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Court {
    @SerializedName("id")
    private int id;

    @SerializedName("id_sports")
    private int id_sports;

    @SerializedName("id_place")
    private int id_place;

    @SerializedName("id_users")
    private int id_users;

    public int getId_users() {
        return id_users;
    }

    public void setId_users(int id_users) {
        this.id_users = id_users;
    }

    @SerializedName("courtNumber")
    private int courtNumber;

    @SerializedName("date")
    private Date date;

    @SerializedName("name")
    private String name;

    @SerializedName("status")
    private String status;

    @SerializedName("entryTime")
    private String entryTime;

    @SerializedName("departureTime")
    private String departureTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_sports() {
        return id_sports;
    }

    public void setId_sports(int id_sports) {
        this.id_sports = id_sports;
    }

    public int getId_place() {
        return id_place;
    }

    public void setId_place(int id_place) {
        this.id_place = id_place;
    }

    public int getCourtNumber() {
        return courtNumber;
    }

    public void setCourtNumber(int courtNumber) {
        this.courtNumber = courtNumber;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
