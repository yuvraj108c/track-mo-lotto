package com.example.track_mo_lotto.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class GpsService extends Service {

    private LocationListener listener;
    private LocationManager locationManager;

    ArrayList<Double> lats = new ArrayList<Double>();
    ArrayList<Double> longs = new ArrayList<Double>();

    ArrayList<Integer> latIsSame = new ArrayList<Integer>();
    ArrayList<Integer> longIsSame = new ArrayList<Integer>();
    private String phoneNo = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //ONCE SERVICE STARTS, INITIALIZE LOCATION LISTENER
    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        phoneNo = preferences.getString("phone","t");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference userRef = db.collection("users").document(phoneNo);



        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //intent filter, main activity will only be listening to intent named "location_update"
                Intent i = new Intent("location_update");


                lats.add( location.getLatitude());
                longs.add(location.getLongitude());


                //ONCE WE'VE HAD 5 REQUESTS, CHECK THE 5 PAIR OF COORDINATES
                if(lats.size() == 5 && longs.size() == 5){

                    double lastLat = location.getLatitude();
                    double lastLong = location.getLongitude();

                    for(int j=0; j<lats.size(); j++){
                        if(lats.get(j) == lastLat){
                            latIsSame.add(1);
                        }

                        if(longs.get(j) == lastLong){
                            longIsSame.add(1);
                        }
                    }

                    //IF ALL PAIR OF COORDINATES FROM THE ARRAYLIST WERE THE SAME, DECREASE THE RATE(IS PROBABLY NOT MOVING)
                    if(latIsSame.size() > 4 ){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0,listener);

                        //ELSE KEEP THE RATE LIKE IT IS( IS MOVING)
                    }else{
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0,listener);
                    }

                    lats.clear();
                    longs.clear();
                    latIsSame.clear();
                    longIsSame.clear();

                }

                //BROADCAST TO MAINACTIVITY
                i.putExtra("coordinates", location.getLatitude()+" "+location.getLongitude());
                sendBroadcast(i);

                //SEND TO FIREBASE
                double latitude = (location.getLatitude());
                double longitude = (location.getLongitude());


                userRef.update("lat",latitude);
                userRef.update("lng",longitude);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            //IF LOCATION IS OFF, DIRECT USER TO SETTINGS TO TURN IT ON
            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        };

        //CONTINUE INITIALIZING LOCATION MANAGER

        //get location service(to get current lat, long)
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0,listener);
    }


    //ONCE SERVICE STOPPED, DEACTIVATE THE LOCATION LISTENER
    @Override
    public void onDestroy(){
        super.onDestroy();

        //unregister the listener with removeUpdates()
        if(locationManager != null){
            locationManager.removeUpdates(listener);
        }
    }
}