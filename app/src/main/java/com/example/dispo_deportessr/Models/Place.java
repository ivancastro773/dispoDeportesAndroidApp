package com.example.dispo_deportessr.Models;

import com.google.gson.annotations.SerializedName;

public class Place {

    @SerializedName("id")
    private int id;

    @SerializedName("id_sports")
    private int id_sports;

    @SerializedName("id_user")
    private int id_user;

    @SerializedName("cant_courts")
    private int cant_courts;

    @SerializedName("name")
    private String name;

    @SerializedName("holidays")
    private int holidays;

    @SerializedName("description")
    private String description;

    @SerializedName("address")
    private String address;

    @SerializedName("phone")
    private String phone;

    @SerializedName("image_url")
    private String image_url;

    @SerializedName("img_id")
    private String img_id;

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImg_id() {
        return img_id;
    }

    public void setImg_id(String img_id) {
        this.img_id = img_id;
    }

    public int getCant_courts() {
        return cant_courts;
    }

    public void setCant_courts(int cant_courts) {
        this.cant_courts = cant_courts;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHolidays() {
        return holidays;
    }

    public void setHolidays(int holidays) {
        this.holidays = holidays;
    }

    public int getId_sports() {
        return id_sports;
    }

    public void setId_sports(int id_sports) {
        this.id_sports = id_sports;
    }
}
