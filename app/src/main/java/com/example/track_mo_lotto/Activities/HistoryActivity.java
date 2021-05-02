package com.example.track_mo_lotto.Activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.track_mo_lotto.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends FragmentActivity implements OnMapReadyCallback {


    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    BottomNavigationView bottomNavigationView;
    private String phone;
    private DocumentReference userDocRef;
    static ArrayList<LatLng> carcoordinates = new ArrayList<>();
    private GoogleMap mMap;
    private PolylineOptions rectOptions = new PolylineOptions();
    private Polyline polyline;
    private ArrayList datesToBeColored = new ArrayList();
    private TextView selectedDateTV;

    public HistoryActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initDatePicker();
        dateButton = findViewById(R.id.picker);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.history);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.live:
                        Intent intent = new Intent(HistoryActivity.this, MapsActivity.class);
                        intent.putExtra("phone", phone);
                        startActivity(intent);
                        overridePendingTransition(0, 0);

                    case R.id.exit:

                        //code to logout user

                }
                return  true;

            }
        });


        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_history);
        mapFragment.getMapAsync(this);

        selectedDateTV = findViewById(R.id.selectedDateTV);


        final ArrayList datesToBeColored = new ArrayList();
//        datesToBeColored.add(Tools.getFormattedDateToday());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userDocRef = db.collection("users").document(phone);

        String current_date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        getCoordinates(current_date);

    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                getCoordinates(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }
    private String makeDateString(int day, int month, int year)
    {
        String monthString = String.valueOf(month);
        if (monthString.length() == 1) {
            monthString = "0" + monthString;
        }
        return day + "-" + monthString + "-" + year;
    }

    private void getCoordinates(String date) {
        final DocumentReference coordinatesDocRef = userDocRef.collection("coordinates").document(date);
        rectOptions = new PolylineOptions();
        if (polyline != null) polyline.remove();

        selectedDateTV.setText("DATE: " + date);

        coordinatesDocRef
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("coordinates", "DocumentSnapshot data: " + document.getData());

                        List<Object> coordinatesObj = Arrays.asList(document.getData().values().toArray());

                        for (int i = 0; i < coordinatesObj.size(); i++) {
                            List<Double> coordinates = (List<Double>) coordinatesObj.get(i);
                            carcoordinates.add(new LatLng(coordinates.get(0), coordinates.get(1)));
                            rectOptions.add(new LatLng(coordinates.get(0), coordinates.get(1)));
                        }

                        Log.d("car coordinates", String.valueOf(carcoordinates));

                        if (carcoordinates.size() > 0) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(carcoordinates.get(0)));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));

                            rectOptions.width(10).color(Color.BLUE);
                            polyline = mMap.addPolyline(rectOptions);
                        }
                    } else {
                        Log.d("FIREBASE ERROR:", "get failed with ", task.getException());
                    }
                }

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void openDatePicker(View view) {

        datePickerDialog.show();

    }
//    public void openDatePicker(View view)
//    {
//        datePickerDialog.show();
//    }
}
