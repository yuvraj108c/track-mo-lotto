package com.example.track_mo_lotto.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.track_mo_lotto.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    BottomNavigationView bottomNavigationView;

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

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.live);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.history:
                        Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                        intent.putExtra("phone", phone);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.exit:
                        Intent exitIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        startActivity(exitIntent);
                        overridePendingTransition(0, 0);
                        break;
                }
                return true;
            }
        });




        getCoordinatesFromDB();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_history);
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
                        myMarker = mMap.addMarker(new MarkerOptions().position(carPosition).title("MY CAR") .icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.car_icon)));
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

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId){
        Drawable VectorDrawable= ContextCompat.getDrawable(context,vectorResId);
        VectorDrawable.setBounds(0,0,VectorDrawable.getIntrinsicWidth(),VectorDrawable.getIntrinsicHeight());
        Bitmap bitmap=Bitmap.createBitmap(VectorDrawable.getIntrinsicWidth(),VectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        VectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

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

