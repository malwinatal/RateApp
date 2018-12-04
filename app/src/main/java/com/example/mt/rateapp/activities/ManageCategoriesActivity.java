package com.example.mt.rateapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mt.rateapp.ItemsOpenHelper;
import com.example.mt.rateapp.MyCategoryRecyclerViewAdapter;
import com.example.mt.rateapp.MyItemRecyclerViewAdapter;
import com.example.mt.rateapp.R;
import com.example.mt.rateapp.models.Category;
import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class ManageCategoriesActivity extends AppCompatActivity implements IconDialog.Callback, MyCategoryRecyclerViewAdapter.OnCategoryListInteractionListener {

    private final static String TITLE_NEW = "New category name";
    private final static String TITLE_EDIT = "Edit category name";
    private final static String ICON_NEW = "New category icon";
    private final static String ICON_EDIT = "Edit category icon";

    public String name;
    List<Category> categories;
    MyCategoryRecyclerViewAdapter adapter;
    private boolean isNew = true;
    private Category categoryToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);

        categories = (List<Category>) getIntent().getSerializableExtra("Categories");

        FloatingActionButton fabAdd = findViewById(R.id.fab_category);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNew = true;
                askName("");
            }
        });

        RecyclerView recyclerView = findViewById(R.id.category_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyCategoryRecyclerViewAdapter(categories,this,this);
        recyclerView.setAdapter(adapter);
    }

    private void askName(String categoryName){
        final View view = getLayoutInflater().inflate(R.layout.dialog_new_category, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ManageCategoriesActivity.this);
        if(isNew)
            alertDialog.setTitle(TITLE_NEW);
        else
            alertDialog.setTitle(TITLE_EDIT);
        //alertDialog.setMessage("Insert name");

        final EditText input = view.findViewById(R.id.dialog_category_name);

        input.setText(categoryName);

        alertDialog.setView(view);
        alertDialog.setPositiveButton("Next",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        name = input.getText().toString();
                        askIcon();
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void askIcon(){
        IconDialog iconDialog = new IconDialog();
        if(isNew)
            iconDialog.setTitle(IconDialog.VISIBILITY_ALWAYS,ICON_NEW);
        else {
            iconDialog.setSelectedIcons(categoryToEdit.iconId);
            iconDialog.setTitle(IconDialog.VISIBILITY_ALWAYS, ICON_EDIT);
        }
        iconDialog.show(getSupportFragmentManager(),"category_icon");
    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        if (icons.length == 1){
            ItemsOpenHelper dbHelper = new ItemsOpenHelper(this);
            if (isNew) {
                Category category = new Category(name, icons[0].getId());
                if (dbHelper.addCategoryToDB(category)) {
                    categories.add(category);
                    adapter.notifyDataSetChanged();
                }
            } else {
                Category newCategory = new Category(categoryToEdit.id, name, icons[0].getId());
                if (dbHelper.editCategoryInDB(newCategory)){
                    categoryToEdit.name = name;
                    categoryToEdit.iconId = icons[0].getId();
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onCategoryEditButtonInteraction(Category category) {
        isNew = false;
        categoryToEdit = category;
        askName(category.name);
    }

    @Override
    public void onCategoryRemoveButtonInteraction(final Category category) {
        new android.app.AlertDialog.Builder(this)
                //.setTitle("Deleting")
                .setMessage("Do you really want to delete this category with all of the items?")
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        ItemsOpenHelper dbHelper = new ItemsOpenHelper(getApplicationContext());
                        dbHelper.deleteCateFromDB(category);
                        categories.remove(category);
                        Log.v("remove", categories.toString());
                        adapter.notifyDataSetChanged();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("Categories", (Serializable) categories);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
