package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {

    ImageButton imageButtonBack;
    AppCompatButton appCompatButtonGui;
    TextInputEditText et_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        KhoiTao();

        imageButtonBack.setOnClickListener(v->{
            finish();
        });
        appCompatButtonGui.setOnClickListener(v->{
            GuiEmail();
        });
    }

    private void KhoiTao() {
        imageButtonBack = findViewById(R.id.btnBackResetPass);
        appCompatButtonGui = findViewById(R.id.btn_Gui);
        et_email = findViewById(R.id.et_resetPass);
    }
    private void GuiEmail(){
        String currentEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        String email = String.valueOf(et_email.getText()).trim();
        if(email.isEmpty()){
            Toast.makeText(this, "Điền email đi bạn ơi!", Toast.LENGTH_SHORT).show();
        }else if(email.equals(currentEmail)) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ResetPasswordActivity.this, "Kiểm tra hòm thư của bạn nha!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }else{
            Toast.makeText(this, "Email này không tồn tại hoặc không đúng định dạng!", Toast.LENGTH_SHORT).show();
        }
    }
}