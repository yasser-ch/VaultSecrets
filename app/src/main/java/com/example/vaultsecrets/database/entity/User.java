package com.example.vaultsecrets.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String email;

    public User(String name, String email) {
        this.name  = name;
        this.email = email;
    }

    public int    getId()    { return id; }
    public void   setId(int id) { this.id = id; }
    public String getName()  { return name; }
    public void   setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void   setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}