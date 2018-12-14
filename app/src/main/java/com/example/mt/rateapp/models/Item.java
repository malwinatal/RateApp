package com.example.mt.rateapp.models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class Item implements Serializable {
    public String name;
    public int score;
    public String notes;
    public String imageUrl;
    public Date date;
    public Category category;

    public Item(Category category, String name, int score, String notes, String imageUrl, Date date) {
        this.category = category;
        this.name = name;
        this.score = score;
        this.notes = notes;
        this.imageUrl = imageUrl;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return score == item.score &&
                Objects.equals(category, item.category) &&
                Objects.equals(name, item.name) &&
                Objects.equals(notes, item.notes) &&
                Objects.equals(imageUrl, item.imageUrl) &&
                Objects.equals(date, item.date);
    }

    @Override
    public int hashCode() {

        return Objects.hash(category, name, score, notes, imageUrl, date);

    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", score=" + score +
                '}';
    }

    public Item clone(){
        return new Item(category, name, score, notes, imageUrl, date);
    }

    public void updateItem(Item item) {
        this.category = item.category;
        this.name = item.name;
        this.score = item.score;
        this.notes = item.notes;
        this.imageUrl = item.imageUrl;
        this.date = item.date;
    }

}
