package com.example.starter.database;


import android.content.Context;

import androidx.room.Room;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.starter.database.entity.FridgeStatusData;
import com.example.starter.database.entity.ReciptData;
import com.example.starter.database.dao.ReciptDataDAO;
import com.example.starter.database.dao.FridgeStatusDataDAO;

@Database(entities = {ReciptData.class, FridgeStatusData.class}, version = 1, exportSchema = false)
public abstract class FooMinderDatabase extends RoomDatabase {
    public abstract ReciptDataDAO getReciptDataDAO();
    public abstract FridgeStatusDataDAO getFridgeStatusDAO();


    private static volatile FooMinderDatabase INSTANCE;

    static FooMinderDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FooMinderDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FooMinderDatabase.class, "FooMinder.db")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
