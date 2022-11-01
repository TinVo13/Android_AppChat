package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmail,txtPassword;
    private Button btnSignIn,btnsignInWithPhone;
    private TextView tvCreate;
    private FirebaseAuth auth;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        KhoiTao();
        tvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(LoginActivity.this, "Email"+txtEmail.getText().toString(), Toast.LENGTH_SHORT).show();
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Login();
                        }else {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        btnsignInWithPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,LoginPhoneActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Login() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        if(email.isEmpty()||!email.contains("@gmail")){
            showError(txtEmail,"Email không đúng định dạng!");
        }else if(password.isEmpty()||password.length()<8){
            showError(txtPassword,"Mật khẩu phải từ 8 ký tự và không được rỗng!");
        }else{
            loadingBar.setTitle("Đang đăng nhập vào tài khoản...") ;
            loadingBar.setMessage("Vui lòng chờ vài giây!");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else if(task.isCanceled()){
                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showError(EditText text, String s) {
        text.setError(s);
        text.requestFocus();
    }

    private void KhoiTao() {
        txtEmail = findViewById(R.id.txtEmailLogin);
        txtPassword = findViewById(R.id.txtPasswordLogin);
        tvCreate = findViewById(R.id.textViewCreate);
        loadingBar = new ProgressDialog(this);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnsignInWithPhone = findViewById(R.id.signin_phonenumber);
        auth = FirebaseAuth.getInstance();
    }
}