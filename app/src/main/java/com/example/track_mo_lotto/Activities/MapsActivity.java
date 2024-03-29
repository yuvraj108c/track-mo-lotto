package com.example.track_mo_lotto.Activities;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.example.track_mo_lotto.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String phone;
    private String TAG = "FIREBASE";
    Marker myMarker;
    static ArrayList<LatLng> carcordinates=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        getCoordinatesFromDB();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void getCoordinatesFromDB(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference userDocRef = db.collection("users").document(phone);

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        final DocumentReference coordinatesDocRef = userDocRef.collection("coordinates").document(date);

        coordinatesDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData().values());

                    String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                    ArrayList<Double> newCoordinates = (ArrayList<Double>) snapshot.getData().get(time);
                    LatLng carPosition = null;

                    if(newCoordinates != null) {
                        carPosition = new LatLng(newCoordinates.get(0), newCoordinates.get(1));
                    }else {
                        List<Object> prevCoordinatesObj = Arrays.asList(snapshot.getData().values().toArray());
                        List<Double> prevCoordinates = (List<Double>) prevCoordinatesObj.get(0);
                        carPosition = new LatLng(prevCoordinates.get(0), prevCoordinates.get(1));
                        Log.d("Previous coordinates: ", String.valueOf(carPosition));
                    }

                    if(carPosition != null){
                        carcordinates.add(carPosition);

                        PolylineOptions drawline= new PolylineOptions().addAll(carcordinates).color(Color.BLUE).width(10);

                        if(myMarker!=null) myMarker.remove();
                        myMarker = mMap.addMarker(new MarkerOptions().position(carPosition).title("MY CAR"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(carPosition));
                        mMap.animateCamera( CameraUpdateFactory.zoomTo( 12.0f ) );

                        Polyline line=mMap.addPolyline(drawline);
                        line.setVisible(true);
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(lat, lng);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//
    }
}

