package com.example.vaultsecrets.database.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "orders",
        indices = {
                @Index(value = {"userId", "date"}),
                @Index(value = "amount")
        }
)
public class Order {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int    userId;
    private long   date;
    private double amount;

    public Order(int userId, long date, double amount) {
        this.userId = userId;
        this.date   = date;
        this.amount = amount;
    }

    public int    getId()     { return id; }
    public void   setId(int id) { this.id = id; }
    public int    getUserId() { return userId; }
    public void   setUserId(int userId) { this.userId = userId; }
    public long   getDate()   { return date; }
    public void   setDate(long date) { this.date = date; }
    public double getAmount() { return amount; }
    public void   setAmount(double amount) { this.amount = amount; }
}