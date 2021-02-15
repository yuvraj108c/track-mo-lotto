package com.example.track_mo_lotto.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.track_mo_lotto.R;
import com.example.track_mo_lotto.Services.GpsService;

public class TrackingActivity extends AppCompatActivity {

    //CODE IS EXPLAINED WITH COMMENTS AT EVERY STEP OF THE WAY
    private Button btn_start, btn_stop;
    private TextView textView;
    private BroadcastReceiver broadcastReceiver;
    private int NOTIFICATION_ID = 0;
    private String phoneNo = "";
    NotificationManager notificationManager;


    //BELOW BROADCAST RECEIVER IS REGISTERED, WILL BE LISTENING FOR AN INTENT NAMED "location_update" SENT FROM THE SERVICE
    @Override
    protected void onResume() {
        super.onResume();

        //2, if the receiver does not exist, create a new one and receive coordinates
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    //send the coordinates to the text view
                    //textView.append("\n" +intent.getExtras().get("coordinates"));
                }
            };
        }

        //1, set the receiver up to receiver an intent named "location_update", if receiver doest not exist go up
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }


    //UNREGISTER THE RECEIVER WHEN DONE
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        Intent intent = getIntent();
        phoneNo = intent.getStringExtra("phone");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String phoneNo = preferences.getString("phone","test");
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, phoneNo, duration);
        toast.show();

        btn_start = (Button) findViewById(R.id.button);
        btn_stop = (Button) findViewById(R.id.button2);
        textView = (TextView) findViewById(R.id.textView);

        if(isMyServiceRunning(GpsService.class)){
            btn_start.setVisibility(View.INVISIBLE);
        }else{
            btn_stop.setVisibility(View.INVISIBLE);
        }

        //CHECK IF PERMISSIONS ARE GRANTED BEFORE ENABLING BUTTONS
        if(!runtime_permissions())
            enable_buttons();
    }

    //METHOD THAT MAKE START AND STOP BUTTON CLICKABLE
    private void enable_buttons() {

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNotification(view);

                Intent i = new Intent(getApplicationContext(), GpsService.class);
                i.putExtra("phonenumber",phoneNo);
                startService(i);

                finishAffinity();
                System.exit(1);
            }
        });


        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(NOTIFICATION_ID);
                Intent i = new Intent(getApplicationContext(), GpsService.class);

                //i.putExtra("phonenumber",phoneNo);
                stopService(i);


                finishAffinity();
                System.exit(0);

            }
        });

    }

    public void createNotification(View view) {
        Intent intent = new Intent(this, TrackingActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentIntent(pIntent)
                .setContentTitle("Track-Mo-Lotto is active")
                .setContentText("Send location").setSmallIcon(R.drawable.ic_launcher_background).build();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_NO_CLEAR;

        notificationManager.notify(NOTIFICATION_ID, noti);

    }

    //METHOD THAT WILL CHECK IF PERMISSION IS GRANTED. IF NOT ASK FOR PERMISSION
    private boolean runtime_permissions() {

        //IF PERMISSION IS NOT GRANTED, ASK FOR THE PERMISSION, GRANT PERMISSION AND RETURN TRUE & CONTINUE TO ONREQUESTPERMISSIONRESULT BELOW
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        //IF PERMISSION WAS ALREADY GRANTED, RETURN FALSE & ENABLE BUTTONS
        return false;
    }

    //ONREQUESTPERMISSIONRESULT METHOD, CHECH IF PERMISSION WAS GRANTED AFTER ASKED
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //IF PERMISSION IS GRANTED, ENABLE BUTTONS
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }else {
                runtime_permissions();
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}