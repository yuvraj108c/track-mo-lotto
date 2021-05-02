package com.example.track_mo_lotto.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.track_mo_lotto.MainActivity;
import com.example.track_mo_lotto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPVerificationActivity extends AppCompatActivity {

    private EditText otpCode1, otpCode2, otpCode3, otpCode4, otpCode5, otpCode6;
    private String verificationID;
    private String phonenumber;
    private String phone;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        final String next_path = intent.getStringExtra("path");
        phonenumber="+230"+phone;

        Log.d("phone",phonenumber);

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final Button buttonVerify = findViewById(R.id.buttonVerifyOTP);

        // The following block of code can be inserted after login button clicked
        // Parameters to change then -> OTPVerificationActivity.this and pass VerificationId as Extra input to this activity
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressBar.setVisibility(View.GONE);
                buttonVerify.setVisibility(View.INVISIBLE);
                Toast.makeText(OTPVerificationActivity.this, "Code could not be sent !", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                verificationID = verificationId;
                Toast.makeText(OTPVerificationActivity.this,"OTP Sent Again",Toast.LENGTH_SHORT).show();
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder()
                        .setPhoneNumber(phonenumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        ///////////

        // Getting the OTP input by user
        otpCode1 = findViewById(R.id.otpCode1);
        otpCode2 = findViewById(R.id.otpCode2);
        otpCode3 = findViewById(R.id.otpCode3);
        otpCode4 = findViewById(R.id.otpCode4);
        otpCode5 = findViewById(R.id.otpCode5);
        otpCode6 = findViewById(R.id.otpCode6);
        setUpOTPInputs();



        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checking for empty inputs
                if (otpCode1.getText().toString().trim().isEmpty()
                        || otpCode2.getText().toString().trim().isEmpty()
                        || otpCode3.getText().toString().trim().isEmpty()
                        || otpCode4.getText().toString().trim().isEmpty()
                        || otpCode5.getText().toString().trim().isEmpty()
                        || otpCode6.getText().toString().trim().isEmpty()) {
                    Toast.makeText(OTPVerificationActivity.this, "Please enter valid code", Toast.LENGTH_SHORT).show();
                    return;
                }

                String code =
                        otpCode1.getText().toString() +
                                otpCode2.getText().toString() +
                                otpCode3.getText().toString() +
                                otpCode4.getText().toString() +
                                otpCode5.getText().toString() +
                                otpCode6.getText().toString();

                if (verificationID != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    buttonVerify.setVisibility(View.INVISIBLE);

                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationID,
                            code
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    buttonVerify.setVisibility(View.VISIBLE);
                                    // If correct OTP inserted, new activity displayed
                                    if (task.isSuccessful()) {
                                        Intent nextIntent;
                                        if(next_path.equals("maps")){
                                            nextIntent= new Intent(getApplicationContext(), MapsActivity.class);

                                        }else{
                                            nextIntent= new Intent(getApplicationContext(), TrackingActivity.class);
                                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("phone",phone);
                                            editor.apply();

                                        }
                                        nextIntent.putExtra("phone",phone);
                                        nextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(nextIntent);
                                    // else display error message
                                    } else {
                                        Toast.makeText(OTPVerificationActivity.this, "The verification code was incorrect", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        findViewById(R.id.txtResend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {

                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        buttonVerify.setVisibility(View.INVISIBLE);
                        Toast.makeText(OTPVerificationActivity.this, "Code could not be sent !", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCodeSent(@NonNull String newverificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        verificationID = newverificationId;
                    }
                };

                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder()
                                .setPhoneNumber(phonenumber)       // Phone number to verify
                                .setTimeout( 60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(OTPVerificationActivity.this)                 // Activity (for callback binding)
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

    }

    // Beautification of code -> request focus after one OTP number inserted
    public void setUpOTPInputs() {
        otpCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() ==1) {
                    otpCode2.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        otpCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0){
                    otpCode1.requestFocus();
                }
                else {
                    if (s.length() ==1) {
                        otpCode3.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        otpCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0){
                    otpCode2.requestFocus();
                }
                else {
                    if (s.length() == 1) {
                        otpCode4.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        otpCode4.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0){
                    otpCode3.requestFocus();
                }
                else if (s.length() ==1) {
                    otpCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        otpCode5.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0){
                    otpCode4.requestFocus();
                }
                else if (s.length() ==1) {
                    otpCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otpCode6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0){
                    otpCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

}
