package com.example.mt.rateapp.models;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id &&
                iconId == category.iconId &&
                Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, iconId);
    }
}
