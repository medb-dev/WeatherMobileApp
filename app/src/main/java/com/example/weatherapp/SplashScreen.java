package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;

public class SplashScreen extends AppCompatActivity {
    View logo,footer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Utils.blackIconStatusBar(SplashScreen.this,R.color.light_Background);

        logo = findViewById(R.id.applogo);
        footer = findViewById(R.id.footer);
        new Handler().postDelayed(() -> {

            Intent intent = new Intent(getApplicationContext(),Login.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    SplashScreen.this,
                    Pair.create(logo,"logo")
            );
            startActivity(
                    intent,
                    options.toBundle()
            );
            finish();
        },3000);
    }
}