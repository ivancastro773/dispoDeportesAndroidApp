package com.example.dispo_deportessr.Models;

public class User {
    private int id;
    private String name;
    private String email;
    private String rol;

    public User(int id, String name, String email,String rol) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }
}
