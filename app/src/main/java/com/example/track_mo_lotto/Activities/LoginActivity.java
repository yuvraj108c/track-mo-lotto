package com.example.track_mo_lotto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.track_mo_lotto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBtn = findViewById(R.id.loginBtn);
        final EditText phoneET = findViewById(R.id.phoneET);
        TextView registerTV = findViewById(R.id.registerTV);
        final TextView errorTV = findViewById(R.id.loginErrorTV);

        registerTV.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), RegisterActivity.class);
                startActivity(myIntent);
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                errorTV.setText("");
                String phone = phoneET.getText().toString();

                if(phone.length() == 0 || phone.length() < 8){
                    errorTV.setText(("Invalid phone number."));
                }else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("users")
                            .document(phone)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d("phone", "DocumentSnapshot data: " + document.getData());
                                } else {
                                    errorTV.setText("Invalid phone number.");
                                }
                            } else {
                                Log.d("FIREBASE ERROR:", "get failed with ", task.getException());
                            }
                        }
                    });
                }


            }
        });

    }
}
