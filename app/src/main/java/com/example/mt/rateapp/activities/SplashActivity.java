package com.example.mt.rateapp.activities;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SystemClock.sleep(7000);
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}
