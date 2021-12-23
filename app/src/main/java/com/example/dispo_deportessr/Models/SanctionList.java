package com.example.dispo_deportessr.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class SanctionList {

    @SerializedName("id")
    private int id;

    @SerializedName("id_users")
    private int id_users;

    @SerializedName("id_place")
    private int id_place;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("sanctionDay")
    private Date sanctionDay;

    @SerializedName("freeDay")
    private Date freeDay;

    public Date getSanctionDay() {
        return sanctionDay;
    }

    public void setSanctionDay(Date sanctionDay) {
        this.sanctionDay = sanctionDay;
    }

    public Date getFreeDay() {
        return freeDay;
    }

    public void setFreeDay(Date freeDay) {
        this.freeDay = freeDay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_users() {
        return id_users;
    }

    public void setId_users(int id_users) {
        this.id_users = id_users;
    }

    public int getId_place() {
        return id_place;
    }

    public void setId_place(int id_place) {
        this.id_place = id_place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
