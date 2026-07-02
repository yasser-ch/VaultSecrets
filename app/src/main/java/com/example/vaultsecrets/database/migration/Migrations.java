package com.example.vaultsecrets.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrations {

    // v1 → v2 : ajout colonne email
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE users ADD COLUMN email TEXT DEFAULT '' NOT NULL");
        }
    };

    // v2 → v3 : nouvelle table devices
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS devices (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "userId INTEGER NOT NULL, " +
                            "model TEXT NOT NULL, " +
                            "FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE)"
            );
            db.execSQL("CREATE INDEX index_devices_userId ON devices (userId)");
        }
    };

    // v3 → v4 : nouvelle table orders + index
    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS orders (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "userId INTEGER NOT NULL, " +
                            "date INTEGER NOT NULL, " +
                            "amount REAL NOT NULL)"
            );
            db.execSQL("CREATE INDEX index_orders_userId_date ON orders (userId, date)");
            db.execSQL("CREATE INDEX index_orders_amount ON orders (amount)");
        }
    };
}