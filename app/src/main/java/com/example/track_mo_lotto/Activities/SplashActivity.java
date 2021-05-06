package com.example.track_mo_lotto.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.track_mo_lotto.R;

public class SplashActivity extends AppCompatActivity {

    Button trackBtn, trackerBtn, historyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        trackBtn = findViewById(R.id.registerBtn);
        trackerBtn = findViewById(R.id.trackerBtn);

        trackBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try{
                    Intent myIntent = new Intent(v.getContext(), LoginActivity.class);
                    myIntent.putExtra("path","maps");
                    startActivity(myIntent);
                }finally{
                    finish();
                }

            }
        });

        trackerBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try{
                    Intent myIntent = new Intent(v.getContext(), LoginActivity.class);
                    myIntent.putExtra("path","tracker");
                    startActivity(myIntent);
                }finally{
                    finish();
                }
            }
        });

    }


}
