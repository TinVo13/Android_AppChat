package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifyPhoneNumberActivity extends AppCompatActivity {
    private EditText et_verify;
    private FloatingActionButton btn_done;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);

        mAuth = FirebaseAuth.getInstance();
        et_verify = findViewById(R.id.et_verify);
        btn_done = findViewById(R.id.btn_done);

        mVerificationId = getIntent().getStringExtra("mVerificationId");

        loadingbar = new ProgressDialog(this);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verifycode = et_verify.getText().toString().trim();
                if(TextUtils.isEmpty(verifycode)){
                    et_verify.setError("Vui lòng điền mã OTP của bạn!");
                }else{
                    loadingbar.setTitle("Xác thực số điện thoại!");
                    loadingbar.setMessage("Vui lòng chờ vài giây...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                    PhoneAuthCredential authCredential = PhoneAuthProvider.getCredential(mVerificationId,verifycode);
                    signInWithPhoneAuthCredential(authCredential);
                }
            }
        });
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingbar.dismiss();
                            Intent intent = new Intent(VerifyPhoneNumberActivity.this,RegisterActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(VerifyPhoneNumberActivity.this, "Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}