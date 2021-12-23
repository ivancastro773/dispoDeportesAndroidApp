package com.example.dispo_deportessr.Models;

import com.google.gson.annotations.SerializedName;

public class Search {

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Search(String name) {
        this.name = name;
    }


}
