package com.example.track_mo_lotto.Activities;

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

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText phoneET = findViewById(R.id.registerPhoneET);
        final EditText carNoET = findViewById(R.id.carNoET);
        final TextView errorTV = findViewById(R.id.registerErrorTV);
        Button registerBtn = findViewById(R.id.registerBtn);
        TextView registerTV = findViewById(R.id.registerTV);

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
                String phone = phoneET.getText().toString();
                String carNo = carNoET.getText().toString();

                if(phone.length() == 0 || carNo.length() == 0){
                    errorTV.setText("Please fill all fields");
                }else if(phone.length() < 8){
                    errorTV.setText("Invalid phone number");
                }else{
                    Log.d("details",phone+carNo);
                }

            }
        });


    }
}
