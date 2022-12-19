package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmail,txtPassword;
    private Button btnSignIn,btnsignInWithPhone;
    private TextView tvCreate,tvForgotPassword;
    private FirebaseAuth auth;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        KhoiTao();
        tvCreate.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
        });

        btnSignIn.setOnClickListener(view -> {
            //Toast.makeText(LoginActivity.this, "Email"+txtEmail.getText().toString(), Toast.LENGTH_SHORT).show();
            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Login();
                }else {
                    Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_LONG).show();
                }
            });
        });
        btnsignInWithPhone.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,LoginPhoneActivity.class);
            startActivity(intent);
        });
        tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,ForgotPassword.class)));
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
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    if(auth.getCurrentUser().isEmailVerified()){
                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else{
                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, "Vui lòng xác thực địa chỉ Email trước khi đăng nhập!", Toast.LENGTH_SHORT).show();
                    }
                }else if(task.isCanceled()){
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không chính xác!", Toast.LENGTH_SHORT).show();
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
        tvForgotPassword = findViewById(R.id.textViewForgot);
    }
    private void checkUser(){
        FirebaseUser mUser = auth.getCurrentUser();
        if(mUser!=null&&mUser.isEmailVerified()){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUser();
    }
}