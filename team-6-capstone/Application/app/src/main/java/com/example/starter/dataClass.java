/* This class is created to keep temporary run time data
 *  and is too be stored into Room database when user closes app
 *  or performance modifications on data
 * */

package com.example.starter;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class dataClass implements  Serializable{
    /*
    put all relevant data in this class
    i.e. table view datas, user info
    access these in activities.
     */


    // instance variables for single item

    private Date itemDate;
    private String itemName;
    private int quantity;
    private Date expiryDate;
    private int expiryDuration;
    private String itemLink;


    dataClass()
    {
        this.itemDate = new Date(0);
        this.itemName = null;
        this.quantity = 1;
        this.expiryDate = new Date(0);
        this.itemLink = null;
        this.expiryDuration = 0;
    }

    dataClass(Date receiptDate, String itemName, int quantity, Date expiryDate, int expiryDuration, String itemLink)
    {
        this.itemDate = receiptDate;
        this.itemName = itemName;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.expiryDuration = expiryDuration;
        this.itemLink = itemLink;
    }
    //get
    public Date getItemDate()
    {
        return this.itemDate;
    }
    public String getItemName()
    {
        return this.itemName;
    }
    public int getItemQuantity()
    {
        return this.quantity;
    }
    public Date getItemExpiryDate()
    {
        return this.expiryDate;
    }
    public int getItemExpiryDuration()
    {
        return this.expiryDuration;
    }
    public String getItemLink()
    {
        return this.itemLink;
    }
    //set


    public void setItemDate(Date date)
    {
        this.itemDate = date;
    }
    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }
    public void setQuantity(int quantity)
    {
        this.quantity = quantity < 0 ? 0 : quantity;
    }
    public void incQuantity()
    {
        this.quantity++;
    }

    public void decQuantity()
    {
        this.quantity = (this.quantity - 1 < 0) ? 0 : --this.quantity;
    }
    public void setExpiryDate(Date expiryDate)
    {
        this.expiryDate = expiryDate;
    }
    public void setExpiryDuration(int expiryDuration)
    {
        this.expiryDuration = expiryDuration;
    }
    public void setItemLink(String url)
    {
        this.itemLink = url;
    }

}
