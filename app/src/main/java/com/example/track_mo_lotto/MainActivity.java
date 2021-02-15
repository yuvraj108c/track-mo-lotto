package com.example.track_mo_lotto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tracking);
        startActivity(new Intent(MainActivity.this, com.example.track_mo_lotto.Activities.TrackingActivity.class));
    }

}
