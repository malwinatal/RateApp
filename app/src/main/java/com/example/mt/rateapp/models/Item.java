package com.example.mt.rateapp.models;

import java.io.Serializable;
import java.util.Date;

public class Item implements Serializable {
    public String name;
    public int score;
    public String notes;
    public String imageUrl;
    public Date date;

    public Item(String name, int score, String notes, String imageUrl, Date date) {
        this.name = name;
        this.score = score;
        this.notes = notes;
        this.imageUrl = imageUrl;
        this.date = date;
    }
}
