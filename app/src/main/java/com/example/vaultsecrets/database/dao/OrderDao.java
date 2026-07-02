package com.example.vaultsecrets.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.vaultsecrets.database.entity.Order;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    long insert(Order order);

    @Query("SELECT * FROM orders WHERE id = :id")
    Order getById(int id);

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY date DESC")
    List<Order> getByUser(int userId);

    @Query("SELECT * FROM orders WHERE amount > :minAmount")
    List<Order> getAboveAmount(double minAmount);

    @Query("SELECT * FROM orders WHERE userId = :userId AND amount > :minAmount ORDER BY date DESC")
    List<Order> getByUserAndMinAmount(int userId, double minAmount);
}