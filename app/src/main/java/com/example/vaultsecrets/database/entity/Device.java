package com.example.vaultsecrets.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "devices",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("userId")
)
public class Device {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int    userId;
    private String model;

    public Device(int userId, String model) {
        this.userId = userId;
        this.model  = model;
    }

    public int    getId()     { return id; }
    public void   setId(int id) { this.id = id; }
    public int    getUserId() { return userId; }
    public void   setUserId(int userId) { this.userId = userId; }
    public String getModel()  { return model; }
    public void   setModel(String model) { this.model = model; }
}