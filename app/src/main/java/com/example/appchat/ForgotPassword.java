package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private EditText et_forgot;
    private Button btn_resetpassword;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        et_forgot = findViewById(R.id.et_forgot);
        btn_resetpassword = findViewById(R.id.btn_resetpassword);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(ForgotPassword.this);
        btn_resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_forgot.getText().toString().trim();
                if (email.isEmpty()){
                    et_forgot.setError("Vui lòng điền Email của bạn!");
                }else{
                    progressDialog.setTitle("Đang reset mật khẩu...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(ForgotPassword.this, "Hãy kiểm tra hòm thư của bạn để reset mật khẩu nhé!", Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(ForgotPassword.this, "Email không tồn tại! Thử lại nhé", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}