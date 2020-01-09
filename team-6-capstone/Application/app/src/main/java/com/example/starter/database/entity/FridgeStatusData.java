package com.example.starter.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.annotation.NonNull;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity (tableName = "fridge_status_data")

@TypeConverters(DateConverter.class)

public class FridgeStatusData {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "alcoholRaw")
    private float alcoholRaw;

    @ColumnInfo(name = "CO2Raw")
    private float CO2Raw;

    @ColumnInfo(name = "temperatureRaw")
    private float temperatureRaw;

    @ColumnInfo(name = "statusLevel")
    private int statusLevel;

    public FridgeStatusData (float alcoholRaw, float CO2Raw, float temperatureRaw, int statusLevel) {
        date = new Date();

        setAlcoholRaw(alcoholRaw);
        setCO2Raw(CO2Raw);
        setTemperatureRaw(temperatureRaw);
        setStatusLevel(statusLevel);

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

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    public float getAlcoholRaw() {
        return alcoholRaw;
    }

    public void setAlcoholRaw(float alcoholRaw) {
        this.alcoholRaw = alcoholRaw;
    }

    public float getCO2Raw() {
        return CO2Raw;
    }

    public void setCO2Raw(float CO2Raw) {
        this.CO2Raw = CO2Raw;
    }

    public float getTemperatureRaw() {
        return temperatureRaw;
    }

    public void setTemperatureRaw(float temperatureRaw) {
        this.temperatureRaw = temperatureRaw;
    }

    public int getStatusLevel() {
        return statusLevel;
    }

    public void setStatusLevel(int statusLevel) {
        this.statusLevel = statusLevel;
    }
}
