package com.example.mt.rateapp.models;

import java.io.Serializable;

public class Category implements Serializable {

    public int id;
    public String name;
    public int iconId;
    public  int numberOfItems;

    public Category(int id, String name, int iconId) {
        this.id = id;
        this.name = name;
        this.iconId = iconId;
    }

    public Category(String name, int iconId) {
        this.name = name;
        this.iconId = iconId;
    }

    public Category(int id, String name, int iconId, int numberOfItems){
        this.id = id;
        this.name = name;
        this.iconId = iconId;
        this.numberOfItems = numberOfItems;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", iconId=" + iconId +
                '}';
    }


}
