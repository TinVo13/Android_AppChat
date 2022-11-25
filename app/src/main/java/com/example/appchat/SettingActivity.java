package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 101;
    private Button btnUpdate;
    private EditText txtUserName,txtStatus;
    private CircleImageView circleImageView;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String currentUserId;
    private DatabaseReference mRef;
    private Toolbar toolbar;
    private Uri imageUri;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Init();
        SetCurrentUser();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
    }

    private void updateUser() {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Đang cập nhât hồ sơ người dùng!");
        progressDialog.setMessage("Vui lòng chờ...");
        progressDialog.show();
        String username = txtUserName.getText().toString();
        String status = txtStatus.getText().toString();
        if (TextUtils.isEmpty(username)){
            Toast.makeText(this, "Vui lòng ghi tên người dùng!", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(status)){
            Toast.makeText(this, "Vui lòng ghi trạng thái!", Toast.LENGTH_SHORT).show();
        }else if(imageUri == null) {
            storageReference.child(currentUserId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    HashMap hashMap = new HashMap<>();
                    hashMap.put("hoten",username);
                    hashMap.put("status",status);
                    mRef.child("Users").child(currentUserId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                finish();
                                Toast.makeText(SettingActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(SettingActivity.this, "Lỗi: "+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
        else if(imageUri!=null){
            storageReference.child(currentUserId).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        storageReference.child(currentUserId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap hashMap = new HashMap<>();
                                hashMap.put("hoten",username);
                                hashMap.put("status",status);
                                hashMap.put("image",uri.toString());
                                mRef.child("Users").child(currentUserId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressDialog.dismiss();
                                            finish();
                                            Toast.makeText(SettingActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(SettingActivity.this, "Lỗi: "+task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    private void SetCurrentUser() {
        mRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    txtUserName.setText(snapshot.child("hoten").getValue().toString());
                    txtStatus.setText(snapshot.child("status").getValue().toString());
                    Picasso.get().load(snapshot.child("image").getValue().toString()).into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Init() {
        btnUpdate = findViewById(R.id.btnCapNhat);
        txtStatus = findViewById(R.id.txtStatus);
        txtUserName = findViewById(R.id.txtUserName);
        circleImageView = findViewById(R.id.circleImageView);
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference();
        toolbar = findViewById(R.id.appbarsetting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sửa hồ sơ người dùng");
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfileImage");
        progressDialog = new ProgressDialog(SettingActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE&&resultCode==RESULT_OK&&data!=null){
            imageUri=data.getData();
            circleImageView.setImageURI(imageUri);
        }
    }
}