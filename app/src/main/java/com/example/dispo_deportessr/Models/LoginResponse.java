package com.example.dispo_deportessr.Models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("rol")
    private String rol;

    @SerializedName("idAdmin")
    private Integer idAdmin;

    public Integer getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(Integer idAdmin) {
        this.idAdmin = idAdmin;
    }

    public LoginResponse(String message, User user) {
        this.message = message;

    }

    public String getRol() {
        return rol;
    }

    public String getMessage() {
        return message;
    }
}
