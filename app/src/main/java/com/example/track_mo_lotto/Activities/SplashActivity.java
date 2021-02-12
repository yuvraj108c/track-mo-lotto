package com.example.track_mo_lotto.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.track_mo_lotto.R;

public class SplashActivity extends AppCompatActivity {

    Button trackBtn, trackerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        trackBtn = findViewById(R.id.trackBtn);
        trackerBtn = findViewById(R.id.trackerBtn);

        trackBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(myIntent);
            }
        });

        trackerBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(myIntent);
            }
        });
    }


}
