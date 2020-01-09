package com.example.starter.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.starter.database.entity.ReciptData;

import java.util.List;

@Dao
public interface ReciptDataDAO {
    @Insert
    void insert(ReciptData... reciptData);

    @Update
    void update(ReciptData... reciptData);

    @Query("SELECT * FROM recipt_data")
    List<ReciptData> getReciptData();

    @Query("DELETE FROM recipt_data")
    void deleteAll();

    @Delete
    void delete(ReciptData reciptData);

    @Query("SELECT * FROM recipt_data order by date asc limit 1 offset :row")
    ReciptData getReciptbyRow(int row);

    @Query("UPDATE recipt_data SET quantity = :newquantity WHERE uid = :id")
    void updateByColumnID(int newquantity, int id);
}
