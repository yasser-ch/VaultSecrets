package com.example.vaultsecrets.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.vaultsecrets.database.dao.DeviceDao;
import com.example.vaultsecrets.database.dao.OrderDao;
import com.example.vaultsecrets.database.dao.UserDao;
import com.example.vaultsecrets.database.entity.Device;
import com.example.vaultsecrets.database.entity.Order;
import com.example.vaultsecrets.database.entity.User;
import com.example.vaultsecrets.database.migration.Migrations;

@Database(
        entities = {User.class, Device.class, Order.class},
        version = 4,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "vault_secrets.db";
    private static AppDatabase instance;

    public abstract UserDao   userDao();
    public abstract DeviceDao deviceDao();
    public abstract OrderDao  orderDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DB_NAME)
                    .addMigrations(
                            Migrations.MIGRATION_1_2,
                            Migrations.MIGRATION_2_3,
                            Migrations.MIGRATION_3_4)
                    .build();
        }
        return instance;
    }
}