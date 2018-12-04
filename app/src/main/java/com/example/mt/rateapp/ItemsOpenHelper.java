package com.example.mt.rateapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mt.rateapp.models.Category;
import com.example.mt.rateapp.models.Item;
import com.example.mt.rateapp.models.SortingTypes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ItemsOpenHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ItemsStorage.db";
    public static final String ITEMS_TABLE_CREATE ="CREATE TABLE IF NOT EXISTS Items (\n"+
            "Name VARCHAR PRIMARY KEY,\n"+
            "Notes VARCHAR,\n"+
            "Rating INTEGER NOT NULL,\n"+
            "CreationDate LONG NOT NULL,\n"+
            "ImagePath VARCHAR NOT NULL\n"+
            ");";

    public static final String CATEGORIES_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS Categories (\n"+
            "Id INTEGER PRIMARY KEY,\n"+
            "Name VARCHAR NOT NULL,\n"+
            "IconId INTEGER NOT NULL\n"+
            ");";

    private Context context;

    public ItemsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(ITEMS_TABLE_CREATE);
            db.execSQL(CATEGORIES_TABLE_CREATE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("drop table if exists Items;");
        db.execSQL("drop table if exists Categories;");
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean addItemToDB(Item item){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", item.name);
        values.put("Notes", item.notes);
        values.put("Rating", item.score);
        values.put("CreationDate", item.date.getTime());
        values.put("ImagePath", item.imageUrl);
        long l = db.insert("Items", null, values);
        db.close();
        return l >= 0;
    }

    public boolean editItemInDB(Item item, Item newItem){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", newItem.name);
        values.put("Notes", newItem.notes);
        values.put("Rating", newItem.score);
        String whereClause = "Name = ?";
        String[] whereArgs = new String[]{item.name};
        try{
            long l = db.update("Items", values, whereClause, whereArgs);
            db.close();
            return l == 1;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public void deleteItem(Item item){
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "Name = ?";
        String[] whereArgs = new String[]{item.name};
        db.delete("Items", whereClause, whereArgs);
        db.close();
    }

    public List<Item> readItemsFromDB(SortingTypes type){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                "Name",
                "Notes",
                "Rating",
                "CreationDate",
                "ImagePath"
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder;

        switch (type) {
            case NAME: sortOrder = "Name ASC";
            break;
            case SCORE: sortOrder = "Rating DESC, Name ASC";
            break;
            case NEWEST: sortOrder = "CreationDate DESC";
            break;
            case OLDEST: sortOrder = "CreationDate ASC";
            break;
            default: sortOrder = "CreationDate ASC";
        }

        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = db.query(
                "Items",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        List<Item> items = new ArrayList<Item>();
        while (cursor.moveToNext()){
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(cursor.getLong(cursor.getColumnIndex("CreationDate")));
            Item i = new Item(cursor.getString(cursor.getColumnIndex("Name")),
                    cursor.getInt(cursor.getColumnIndex("Rating")),
                    cursor.getString(cursor.getColumnIndex("Notes")),
                    cursor.getString(cursor.getColumnIndex("ImagePath")),
                    c.getTime());
            items.add(i);
        }
        return items;
    }

    public boolean addCategoryToDB(Category category){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", category.name);
        values.put("IconId", category.iconId);
        long l = db.insert("Categories", null, values);
        category.id=(int)l;
        db.close();
        return l >= 0;
    }

    public void deleteCateFromDB(Category category){
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "Id = "+category.id;
        db.delete("Categories", whereClause, null);
        db.close();
    }

    public List<Category> readCategoriesFromDB(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                "Id",
                "Name",
                "IconId"
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = "Id ASC";

        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = db.query(
                "Categories",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        List<Category> categories = new ArrayList<>();
        while (cursor.moveToNext()){
            Category i = new Category(cursor.getInt(cursor.getColumnIndex("Id")),
                    cursor.getString(cursor.getColumnIndex("Name")),
                    cursor.getInt(cursor.getColumnIndex("IconId")));
            categories.add(i);
        }
        return categories;
    }

    public boolean editCategoryInDB(Category newCategory){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", newCategory.name);
        values.put("IconId", newCategory.iconId);
        String whereClause = "Id = " + newCategory.id;
        try{
            long l = db.update("Categories", values, whereClause, null);
            db.close();
            return l == 1;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}