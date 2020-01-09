package com.example.starter.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.starter.database.entity.FridgeStatusData;

import java.util.List;

@Dao
public interface FridgeStatusDataDAO {
    @Insert
    void insert(FridgeStatusData... fridgeStatusData);

    @Update
    void update(FridgeStatusData... fridgeStatusData);

    @Query("SELECT * FROM fridge_status_data")
    List<FridgeStatusData> getFridgeStatusData();

    @Query("DELETE FROM fridge_status_data")
    void deleteAll();

    @Query("SELECT * FROM fridge_status_data order by date asc limit 1 offset :row")
    FridgeStatusData getFridgeStatusbyRow(int row);
}
