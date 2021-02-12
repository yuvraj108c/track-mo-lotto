package com.example.track_mo_lotto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.track_mo_lotto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText phoneET = findViewById(R.id.registerPhoneET);
        final EditText carNoET = findViewById(R.id.carNoET);
        final TextView errorTV = findViewById(R.id.registerErrorTV);
        final Button registerBtn = findViewById(R.id.registerBtn);
        TextView registerTV = findViewById(R.id.registerTV);
        final ProgressBar progressBar = findViewById(R.id.registerProgressBar);

        progressBar.setVisibility(View.INVISIBLE);

        registerTV.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try{
                    Intent myIntent = new Intent(v.getContext(), LoginActivity.class);
                    startActivity(myIntent);
                }finally {
                    finish();
                }
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                errorTV.setText("");
                final String phone = phoneET.getText().toString();
                String carNo = carNoET.getText().toString();

                if(phone.length() == 0 || carNo.length() == 0){
                    errorTV.setText("Please fill all fields");
                }else if(phone.length() < 8){
                    errorTV.setText("Invalid phone number");
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    registerBtn.setVisibility(View.INVISIBLE);

                    final Map<String, Object> user = new HashMap<>();
                    user.put("car_no",carNo);
                    user.put("lng",0);
                    user.put("lat",0);

                    final FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("users")
                            .document(phone)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    errorTV.setText("Phone number already in use");
                                    progressBar.setVisibility(View.INVISIBLE);
                                    registerBtn.setVisibility(View.VISIBLE);
                                } else {
                                    db.collection("users").document(phone)
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("FIREBASE:", "DocumentSnapshot successfully written!");
                                                    try{
                                                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                                        startActivity(intent);
                                                    }finally {
                                                        finish();
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("FIREBASE:", "Error writing document", e);
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    registerBtn.setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                            } else {
                                Log.d("FIREBASE ERROR:", "get failed with ", task.getException());
                                progressBar.setVisibility(View.INVISIBLE);
                                registerBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    });


                }

            }
        });


    }
}
