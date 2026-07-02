package com.example.vaultsecrets.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.vaultsecrets.database.entity.Device;

import java.util.List;

@Dao
public interface DeviceDao {
    @Insert
    long insert(Device device);

    @Query("SELECT * FROM devices WHERE id = :id")
    Device getById(int id);

    @Query("SELECT * FROM devices WHERE userId = :userId")
    List<Device> getByUserId(int userId);
}