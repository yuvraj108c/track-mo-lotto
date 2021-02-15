package com.example.track_mo_lotto.Services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class GpsService extends IntentService {

    private LocationListener listener;
    private LocationManager locationManager;
    private BroadcastReceiver broadcastReceiver;

    ArrayList<Double> lats = new ArrayList<Double>();
    ArrayList<Double> longs = new ArrayList<Double>();

    ArrayList<Integer> latIsSame = new ArrayList<Integer>();
    ArrayList<Integer> longIsSame = new ArrayList<Integer>();
    private String phoneNo = "";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GpsService(String name) {
        super(name);
    }

    //private String phoneNo = intent.getStringExtra("phone");
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        phoneNo = intent.getStringExtra("phonenumber");
    }


    //ONCE SERVICE STARTS, INITIALIZE LOCATION LISTENER
    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(){


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
                //i.putExtra("coordinates", location.getLatitude()+" "+location.getLongitude());
                //sendBroadcast(i);

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
