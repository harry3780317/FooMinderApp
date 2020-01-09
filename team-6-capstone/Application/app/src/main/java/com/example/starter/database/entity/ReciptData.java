package com.example.starter.database.entity;

import android.widget.TextView;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.TypeConverters;
import androidx.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "recipt_data")

@TypeConverters(DateConverter.class)

public class ReciptData implements Comparable<ReciptData>{

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "item")
    private String item;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "storingTime")
    private int storingTime;

    @ColumnInfo(name = "expirydate")
    private Date expirydate;

    public ReciptData (String item, int quantity, int storingTime) {
        date = new Date();

        setItem(item);
        setQuantity(quantity);
        setStoringTime(storingTime);

    }

    public ReciptData(int newQuanity, int id)
    {
        setQuantity(newQuanity);
        setUid(id);
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    @NonNull
    public  Date getExpirydate()
    {
        return expirydate;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    public void setExpirydate(@NonNull Date date)
    {
        this.expirydate = date;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStoringTime() {
        return storingTime;
    }

    public void setStoringTime(int storingTime) {
        this.storingTime = storingTime;
    }

    //public void setDate(TextView date_in_record){}

    @Override
    public int compareTo(ReciptData o) {
        if(this.expirydate == null || o.getExpirydate() == null)
            return 0;
        return this.expirydate.compareTo(o.getExpirydate());
    }
}
