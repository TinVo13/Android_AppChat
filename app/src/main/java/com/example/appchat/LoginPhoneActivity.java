package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginPhoneActivity extends AppCompatActivity {
    private FloatingActionButton btnNext;
    private EditText et_phone;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callback;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);
        btnNext = findViewById(R.id.btnNext);
        et_phone = findViewById(R.id.et_phone);
        mAuth = FirebaseAuth.getInstance();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = et_phone.getText().toString();
                if(TextUtils.isEmpty(phoneNumber)){
                    et_phone.setError("Vui lòng nhập số điện thoại của bạn!");
                }else{
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            60l,
                            TimeUnit.SECONDS,
                            LoginPhoneActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    Toast.makeText(LoginPhoneActivity.this, "Thanh cong", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginPhoneActivity.this,VerifyPhoneNumberActivity.class);
                                    intent.putExtra("mVerificationId",mVerificationId);
                                    startActivity(intent);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    Toast.makeText(LoginPhoneActivity.this, "That bai"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    mVerificationId = s;
                                    mResendToken = forceResendingToken;
                                }
                            }
                    );
                }
            }
        });
    }
}