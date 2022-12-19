package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtEmail,txtPassword,txtXacNhan;
    private Button btnRegister;
    private FirebaseAuth auth;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        khoiTao();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
            }
        });

    }

    private void Register() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String xacnhan = txtXacNhan.getText().toString();
        if(email.isEmpty()||!email.contains("@gmail")){
            showError(txtEmail,"Email không đúng!");
        }else if(password.isEmpty()||password.length()<8){
            showError(txtPassword,"Mật khẩu phải từ 8 ký tự!");
        }else if(xacnhan.isEmpty()||!xacnhan.equals(password)){
            showError(txtXacNhan,"Xác nhận mật khẩu không hợp lệ!");
        }else{
            loadingBar.setTitle("Đang đang kí tài khoản...") ;
            loadingBar.setMessage("Vui lòng chờ vài giây!");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            checkUserExist(email,password);
        }
    }
    private void checkUserExist(String email,String password){
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
            if(isNewUser){
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(v -> {
                    if(task.isSuccessful()){
                        FirebaseUser fuser = auth.getCurrentUser();
                        assert fuser != null;
                        fuser.sendEmailVerification().addOnSuccessListener(unused -> {
                            loadingBar.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Email not send "+e.getMessage(), Toast.LENGTH_SHORT).show());

                    }else if(task.isCanceled()){
                        loadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Email đã tồn tại! Vui lòng chọn 1 email khác.", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                loadingBar.dismiss();
                Toast.makeText(this, "Email đã được sử dụng. Vui lòng chọn 1 email khác!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showError(EditText field, String s) {
        field.setError(s);
        field.requestFocus();
    }

    private void khoiTao(){
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword =findViewById(R.id.txtPassword);
        txtXacNhan = findViewById(R.id.txtXacNhan);
        btnRegister = findViewById(R.id.btnRegister);
        loadingBar = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
    }
}