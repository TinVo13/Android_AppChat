package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    //PERMISSION ARRAY
    private String[] cameraPermission;
    private String[] storagePermission;

    private EditText editTenGroup,editMoTa;
    private FloatingActionButton btnDone;
    private FirebaseAuth auth;
    private Toolbar toolbar;
    private CircleImageView imgView;
    private Uri uri = null;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        editTenGroup = findViewById(R.id.edit_TenNhom);
        editMoTa = findViewById(R.id.edit_MoTa);
        btnDone = findViewById(R.id.btnDone);
        btnDone.setOnClickListener(this);

        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Add Group");

        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission
                .WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        imgView = findViewById(R.id.circleImageView3);
        imgView.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        checkUser();
    }

    private void checkUser() {
        if(auth.getCurrentUser()!=null){
            toolbar.setSubtitle(auth.getCurrentUser().getEmail());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDone:{
                progressBar = new ProgressDialog(this);
                progressBar.setMessage("Đang tạo nhóm...");
                //ten nhom + mota
                String tenNhom = editTenGroup.getText().toString().trim();
                String moTa = editMoTa.getText().toString().trim();
                //validation
                if(tenNhom.isEmpty()||moTa.isEmpty()){
                    Toast.makeText(this, "Vui lòng ghi tên nhóm và mô tả của nhóm!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.show();

                //timetamp img, idNhom, thoi gian tao
                String thoigian = ""+System.currentTimeMillis();
                if(uri == null){
                    //tao nhom khong co hinh
                    createGroup(""+thoigian,""+tenNhom,""+moTa,"");
                }else{
                    //tao nhom khi co hinh
                    String fileName = "Group_Imgs/"+"image"+thoigian;
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileName);
                    storageReference.putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //lay url cua hinh vua upload
                                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                    while(!uri.isSuccessful());
                                    Uri downloadUri = uri.getResult();
                                        if(uri.isSuccessful()){
                                            createGroup(""+thoigian,""+tenNhom,""+moTa,""+downloadUri);
                                        }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.dismiss();
                                    Toast.makeText(GroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                break;
            }
            case R.id.circleImageView3:{
               if(checkStoragePermission()){
                   requestStoragePermission();
               }else{
                   Intent intent = new Intent();
                   intent.setType("image/*");
                   intent.setAction(Intent.ACTION_GET_CONTENT);
                   startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICK_GALLERY_CODE);
               }
               break;
            }
        }
    }

    private void createGroup(String thoigian, String tenNhom,String mota,String anhNhom) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("groupId",thoigian);
        hashMap.put("groupTitle",tenNhom);
        hashMap.put("groupDes",mota);
        hashMap.put("groupImg",anhNhom);
        hashMap.put("timestamp",""+thoigian);
        hashMap.put("createBy",auth.getUid());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(thoigian).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //tao moi nhom
                //them thanh vien
                HashMap<String,String> hashMap1 = new HashMap<>();
                hashMap1.put("uid",auth.getUid());
                hashMap1.put("role","creator");
                hashMap1.put("timestamp",thoigian);

                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
                reference1.child(thoigian).child("Participants").child(auth.getUid())
                        .setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressBar.dismiss();
                                Toast.makeText(GroupActivity.this, "Tạo nhóm thành công!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.dismiss();
                                Toast.makeText(GroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //that bai
                progressBar.dismiss();
                Toast.makeText(GroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                uri = data.getData();
                imgView.setImageURI(uri);
            }
        }
    }
    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean accept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(accept){
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICK_GALLERY_CODE);
                    }else{
                        Toast.makeText(this, "Vui lòng cấp quyền sử dụng bộ nhớ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}