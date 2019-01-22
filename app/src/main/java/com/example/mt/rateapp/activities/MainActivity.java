package com.example.mt.rateapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mt.rateapp.ItemsOpenHelper;
import com.example.mt.rateapp.R;
import com.example.mt.rateapp.fragments.AddingFragment;
import com.example.mt.rateapp.fragments.EditingFragment;
import com.example.mt.rateapp.fragments.ItemDetailFragment;
import com.example.mt.rateapp.fragments.ItemFragment;
import com.example.mt.rateapp.fragments.ViewPagerFragment;
import com.example.mt.rateapp.models.Category;
import com.example.mt.rateapp.models.Item;

import com.example.mt.rateapp.models.SortingTypes;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconHelper;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ItemFragment.OnListFragmentInteractionListener,
        AddingFragment.OnFragmentInteractionListener, ItemDetailFragment.OnFragmentInteractionListener, EditingFragment.OnFragmentInteractionListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_CATEGORY_LIST = 2;

    static final int ANIM_DURATION_NORMAL = 400;
    static final int ANIM_DURATION_SHORT = 0;
    static final String SHARED_PREFERENCES_CATEGORY = "preferences_key_category";

    private FloatingActionButton fab;
    private List<Item> items;
    private ItemFragment fragment;
    private ViewPagerFragment vpFragment;
    private int toolbarHeight;
    private Toolbar toolbar;
    private ValueAnimator mVaActionBar;
    private TessBaseAPI tessBaseApi;
    private ItemsOpenHelper dbHelper;
    private Menu menu;
    private SortingTypes sortingType = SortingTypes.OLDEST;
    private List<Category> categories;
    private Category recentCategory;
    private SharedPreferences sharedPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //deleteDatabase(ItemsOpenHelper.DATABASE_NAME);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        int recentCategoryIndex = sharedPref.getInt(SHARED_PREFERENCES_CATEGORY, 0);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new ItemsOpenHelper(this);
        categories = dbHelper.readCategoriesFromDB();
        if(!categories.isEmpty()) {
            if(recentCategoryIndex < categories.size())
                recentCategory = categories.get(recentCategoryIndex);
            else
                recentCategory = categories.get(0);
            items = dbHelper.readItemsFromDB(sortingType, recentCategory);
        } else
            items = new ArrayList<>();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!categories.isEmpty())
                    dispatchTakePictureIntent();
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Add category. \n\nCannot add an item without any category.", Toast.LENGTH_SHORT);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null)
                        v.setGravity(Gravity.CENTER);
                    toast.show();
                }
//                Snackbar.make(view, "Test", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        loadDrawer();

        fragment = ItemFragment.newInstance(items);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, ItemFragment.class.getName()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            if (getSupportFragmentManager().getBackStackEntryCount() == 0){
                fab.show();
                showActionBar(ANIM_DURATION_NORMAL);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            return true;
        } else if (id == R.id.action_sort_name){
            sortingType = SortingTypes.NAME;
            updateList();
            menu.findItem(R.id.action_sort).getSubMenu().setGroupEnabled(item.getGroupId(), true);
            item.setEnabled(false);
            return true;
        } else if (id == R.id.action_sort_score){
            sortingType = SortingTypes.SCORE;
            updateList();
            menu.findItem(R.id.action_sort).getSubMenu().setGroupEnabled(item.getGroupId(), true);
            item.setEnabled(false);
            return true;
        } else if (id == R.id.action_sort_new){
            sortingType = SortingTypes.NEWEST;
            updateList();
            menu.findItem(R.id.action_sort).getSubMenu().setGroupEnabled(item.getGroupId(), true);
            item.setEnabled(false);
            return true;
        } else if (id == R.id.action_sort_old){
            sortingType = SortingTypes.OLDEST;
            updateList();
            menu.findItem(R.id.action_sort).getSubMenu().setGroupEnabled(item.getGroupId(), true);
            item.setEnabled(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        hideKeyboard();
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
            if (getSupportFragmentManager().getBackStackEntryCount() > 0){
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fab.show();
                showActionBar(ANIM_DURATION_SHORT);
            }
        } else if (id == R.id.menu_manage_categories) {
            Intent intent = new Intent(MainActivity.this,ManageCategoriesActivity.class);
            intent.putExtra("Categories", (Serializable) categories);
            startActivityForResult(intent,REQUEST_CATEGORY_LIST);
            if (getSupportFragmentManager().getBackStackEntryCount() > 0){
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fab.show();
                showActionBar(ANIM_DURATION_SHORT);
            }
        } else for (Category c:categories)
            if (id == c.id){
                item.setChecked(true);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(SHARED_PREFERENCES_CATEGORY, categories.indexOf(c));
                editor.commit();
                recentCategory = c;
                updateList();
                if (getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fab.show();
                    showActionBar(ANIM_DURATION_NORMAL);
                }
            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelectedInteraction(Item item) {
        vpFragment = ViewPagerFragment.newInstance(items.indexOf(item), items);
        addFragmentFromBottom(vpFragment);
        fab.hide();
        hideActionBar(ANIM_DURATION_NORMAL);
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("ACTIVITY_RESULT",""+requestCode);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            View view = findViewById(R.id.fragment_container);
//            Snackbar.make(view, "I love malmal", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            //mImageView.setImageBitmap(imageBitmap);
            hideActionBar(ANIM_DURATION_SHORT);
            AddingFragment fragment = AddingFragment.newInstance(imageBitmap, recentCategory);
            addFragmentFrom(fragment);
            fab.hide();
        } else if (requestCode == REQUEST_CATEGORY_LIST && resultCode == RESULT_OK) {
            categories = (List<Category>) data.getSerializableExtra("Categories");
//            Log.v("On_activity_result", resultCode+"");
//            Log.v("activities",categories.toString());
            loadDrawer();
        }
    }

    @Override
    public void onSaveButtonInteraction(Item item) {
        ItemsOpenHelper dbHelper = new ItemsOpenHelper(this);
        if(dbHelper.addItemToDB(item)){
            updateList();
            recentCategory.numberOfItems++;
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.pop_enter, R.anim.pop_exit)
                    .remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container))
                    .commit();
            getSupportFragmentManager().popBackStack();
            hideKeyboard();
            showActionBar(ANIM_DURATION_NORMAL);
            fab.show();
        } else {
            Toast.makeText(this, "Use different name", Toast.LENGTH_SHORT).show();
        }
    }

    private void addFragmentFromBottom(Fragment fragment){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        if(backStateName.equals(ItemFragment.class.getName()))
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        else {
            //manager.popBackStack(backStateName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentTransaction ft = manager.beginTransaction();
            ft.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.pop_enter, R.anim.pop_exit);
            ft.add(R.id.fragment_container, fragment);
            ft.addToBackStack(backStateName);
            ft.commitAllowingStateLoss();
        }
    }

    private void addFragmentFrom(Fragment fragment){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        if(backStateName.equals(ItemFragment.class.getName()))
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        else {
            //manager.popBackStack(backStateName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentTransaction ft = manager.beginTransaction();
            ft.setCustomAnimations(0, R.anim.pop_exit, R.anim.pop_enter, R.anim.pop_exit);
            ft.add(R.id.fragment_container, fragment);
            ft.addToBackStack(backStateName);
            ft.commitAllowingStateLoss();
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void removeItemInteraction(final Item item) {

        new AlertDialog.Builder(this)
                //.setTitle("Deleting")
                .setMessage("Do you really want to delete this item?")
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        ItemsOpenHelper dbHelper = new ItemsOpenHelper(getApplicationContext());
                        File file = new File(item.imageUrl);
                        dbHelper.deleteItem(item);
                        items.remove(item);
                        recentCategory.numberOfItems--;
                        fragment.notifyDataSetChange();
                        file.delete();
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.pop_enter, R.anim.pop_exit)
                                .remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container))
                                .commit();
                        getSupportFragmentManager().popBackStack();
                        //hideKeyboard();
                        fab.show();
                        showActionBar(ANIM_DURATION_NORMAL);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void editItemInteraction(Item item) {
        Fragment fragment = EditingFragment.newInstance(item);
        addFragmentFromBottom(fragment);
    }

    void hideActionBar(int mAnimDuration) {
        // initialize `toolbarHeight`
        if (toolbarHeight == 0) {
            toolbarHeight = toolbar.getHeight();
        }

        if (mVaActionBar != null && mVaActionBar.isRunning()) {
            // we are already animating a transition - block here
            return;
        }

        // animate `Toolbar's` height to zero.
        mVaActionBar = ValueAnimator.ofInt(toolbarHeight , 0);
        mVaActionBar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // update LayoutParams
                ((AppBarLayout.LayoutParams)toolbar.getLayoutParams()).height
                        = (Integer)animation.getAnimatedValue();
                toolbar.requestLayout();
            }
        });

        mVaActionBar.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (getSupportActionBar() != null) { // sanity check
                    getSupportActionBar().hide();
                }
            }
        });

        mVaActionBar.setDuration(mAnimDuration);
        mVaActionBar.start();
    }

    void showActionBar(int mAnimDuration) {
        if (mVaActionBar != null && mVaActionBar.isRunning()) {
            // we are already animating a transition - block here
            return;
        }

        // restore `Toolbar's` height
        mVaActionBar = ValueAnimator.ofInt(0 , toolbarHeight);
        mVaActionBar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // update LayoutParams
                ((AppBarLayout.LayoutParams)toolbar.getLayoutParams()).height
                        = (Integer)animation.getAnimatedValue();
                toolbar.requestLayout();
            }
        });

        mVaActionBar.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

                if (getSupportActionBar() != null) { // sanity check
                    getSupportActionBar().show();
                }
            }
        });

        mVaActionBar.setDuration(mAnimDuration);
        mVaActionBar.start();
    }

    @Override
    public void onEditInteraction(Item item, Item newItem) {
        ItemsOpenHelper dbHelper = new ItemsOpenHelper(this);
        if(dbHelper.editItemInDB(item, newItem)){
            //items.set(items.indexOf(item), newItem);
            item.updateItem(newItem);
            updateList();
            vpFragment.notifyDataSetChange(items.indexOf(newItem));
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.pop_enter, R.anim.pop_exit)
                    .remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container))
                    .commit();
            getSupportFragmentManager().popBackStack();
            hideKeyboard();
        } else {
            Toast.makeText(this, "Use different name", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateList(){
        List<Item> newList;
        newList = dbHelper.readItemsFromDB(sortingType, recentCategory);
        items.clear();
        items.addAll(newList);
        if(fragment != null)
            fragment.notifyDataSetChange();
    }

    private void loadDrawer() {
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final Menu menu = navigationView.getMenu().findItem(R.id.menu_categories_list).getSubMenu();
        menu.clear();
        if (categories.size() == 0) {
            menu.add(R.id.menu_categories_group_list, R.id.no_categories, 0, R.string.menu__no_categories);
        } else {
            final IconHelper iconHelper = IconHelper.getInstance(this);
            Log.v("DATA_LOADED ", ""+categories.toString());
            if (iconHelper.isDataLoaded()){
                for (Category c : categories) {
                    MenuItem menuItem = menu.add(R.id.menu_categories_group_list, c.id, c.id, c.name).setCheckable(true);
                    Drawable drawable = iconHelper.getIcon(c.iconId).getDrawable(MainActivity.this);
                    menuItem.setIcon(drawable);
                    if (c.equals(recentCategory)) {
                        //                            menuItem.setChecked(true);
                        navigationView.setCheckedItem(c.id);
                        menu.performIdentifierAction(c.id, 0);
                    }
                }
            }
            else
                iconHelper.addLoadCallback(new IconHelper.LoadCallback() {
                    @Override
                    public void onDataLoaded() {
                        for (Category c : categories) {
                            MenuItem menuItem = menu.add(R.id.menu_categories_group_list, c.id, c.id, c.name).setCheckable(true);
                            Drawable drawable = iconHelper.getIcon(c.iconId).getDrawable(MainActivity.this);
                            menuItem.setIcon(drawable);
                            if (c.equals(recentCategory)) {
    //                            menuItem.setChecked(true);
                                navigationView.setCheckedItem(c.id);
                                menu.performIdentifierAction(c.id, 0);
                            }
                        }
                    }
                });
        }
    }
}
