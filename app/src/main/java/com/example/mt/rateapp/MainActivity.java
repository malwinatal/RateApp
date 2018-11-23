package com.example.mt.rateapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import android.widget.Toast;

import com.example.mt.rateapp.fragments.AddingFragment;
import com.example.mt.rateapp.fragments.EditingFragment;
import com.example.mt.rateapp.fragments.ItemDetailFragment;
import com.example.mt.rateapp.fragments.ItemFragment;
import com.example.mt.rateapp.fragments.ViewPagerFragment;
import com.example.mt.rateapp.models.Item;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ItemFragment.OnListFragmentInteractionListener,
        AddingFragment.OnFragmentInteractionListener, ItemDetailFragment.OnFragmentInteractionListener, EditingFragment.OnFragmentInteractionListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private FloatingActionButton fab;
    List<Item> items;
    ItemFragment fragment;
    ViewPagerFragment vpFragment;
    int toolbarHeight, mAnimDuration = 400/* milliseconds */;
    Toolbar toolbar;
    ValueAnimator mVaActionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        

        ItemsOpenHelper dbHelper = new ItemsOpenHelper(this);
        items = dbHelper.readItemsFromDB();

        //items.add(new Item("Test",  1, "djijsidsfjsdifj", "other", Calendar.getInstance().getTime()));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
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
                showActionBar();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelectedInteraction(Item item) {
        vpFragment = ViewPagerFragment.newInstance(items.indexOf(item), items);
        addFragmentFromBottom(vpFragment);
        fab.hide();
        hideActionBar();
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            View view = findViewById(R.id.fragment_container);
//            Snackbar.make(view, "I love malmal", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            //mImageView.setImageBitmap(imageBitmap);

            AddingFragment fragment = AddingFragment.newInstance(imageBitmap);
            addFragmentFrom(fragment);
            fab.hide();
            getSupportActionBar().hide();
        }
    }

    @Override
    public void onSaveButtonInteraction(Item item) {
        ItemsOpenHelper dbHelper = new ItemsOpenHelper(this);
        if(dbHelper.addItemToDB(item)){
            items.add(item);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.pop_enter, R.anim.pop_exit)
                    .remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container))
                    .commit();
            getSupportFragmentManager().popBackStack();
            hideKeyboard();
            fab.show();
            showActionBar();
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
                        fragment.notifyDataSetChange();
                        file.delete();
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.pop_enter, R.anim.pop_exit)
                                .remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container))
                                .commit();
                        getSupportFragmentManager().popBackStack();
                        //hideKeyboard();
                        fab.show();
                        showActionBar();
                    }})
                .setNegativeButton(android.R.string.no, null).show();

    }

    @Override
    public void editItemInteraction(Item item) {
        Fragment fragment = EditingFragment.newInstance(item);
        addFragmentFromBottom(fragment);
    }

    void hideActionBar() {
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

    void showActionBar() {
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
            fragment.notifyDataSetChange();
            vpFragment.notifyDataSetChange();
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
}
