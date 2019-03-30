package com.example.mt.rateapp.utils;

import android.app.Activity;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;

import com.example.mt.rateapp.R;
import com.example.mt.rateapp.activities.MainActivity;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;

public class TutorialHelper {

    static Animation fadeIn;

    public static void startTutorial(final Activity activity){
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000);

        final ShowcaseView showcaseView = new ShowcaseView.Builder(activity)
                .blockAllTouches()
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Hi")
                .setContentText("Welcome to my tutorial")
                .build();
        showcaseView.setAnimation(fadeIn);
        final DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                showcaseView.hide();
                tutorialFirstPage(activity);
                drawer.removeDrawerListener(this);
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

        showcaseView.overrideButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START, true);
            }
        });
    }

    private static void tutorialFirstPage(final Activity activity){

        Target target = new Target() {
            @Override
            public Point getPoint() {
                NavigationView navigationView = activity.findViewById(R.id.nav_view);
                MenuItem manageCatrgoriesItem = navigationView.getMenu().findItem(R.id.menu_manage_categories);
                View view = manageCatrgoriesItem.getActionView();
                int[] arr = new int[2];
                view.getLocationInWindow(arr);
                int x = arr[0]/2 - manageCatrgoriesItem.getIcon().getIntrinsicWidth()/2;
                int y = arr[1]+view.getHeight()/2;
                return new Point(x, y);
            }
        };

        ShowcaseView showcaseView = new ShowcaseView.Builder(activity)
                .blockAllTouches()
                .setStyle(R.style.CustomShowcaseTheme)
                .setTarget(target)
                .setContentTitle("ShowcaseView")
                .setContentText("This is highlighting the Home button")
                .build();
        showcaseView.setAnimation(fadeIn);
    }
}
