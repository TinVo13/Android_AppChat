package com.example.appchat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 101;
    private CircleImageView circleImageView;
    private Button btnXacNhanTT,btnChonNgay;
    private EditText txtHoTen,txtNgaySinh,txtSDT;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private StorageReference storageReference;
    final int selectedYear = 2001,selectedMonth = 2,selectedDayOfMonth = 13;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        KhoiTao();
        circleImageView.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,REQUEST_CODE);
        });
        btnChonNgay.setOnClickListener(c->{
            openDialog();
        });
        btnXacNhanTT.setOnClickListener(view -> saveData());
    }
    private void openDialog(){
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> txtNgaySinh.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        DatePickerDialog datePickerDialog = new DatePickerDialog(SetupActivity.this,
                android.R.style.Theme_Material_Dialog,
                dateSetListener, selectedYear, selectedMonth, selectedDayOfMonth);

        datePickerDialog.show();
    }

    private void saveData() {
        String hoten = txtHoTen.getText().toString();
        String ngaysinh = txtNgaySinh.getText().toString();
        String sdt = txtSDT.getText().toString();
        if(hoten.isEmpty()){
            showError(txtHoTen,"Họ tên không được rỗng!");
        }else if(ngaysinh.isEmpty()){
            showError(txtNgaySinh,"Ngày sinh không được rỗng!");
        }else if(sdt.length() != 10){
            showError(txtSDT,"Số điện thoại không đúng!");
        }else if(imageUri==null){
            Toast.makeText(this, "Vui lòng chọn 1 hình", Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Đang lưu thông tin người dùng!");
            loadingBar.setMessage("Vui lòng đợi trong giây lát!");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            storageReference.child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    storageReference.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(uri -> {
                        HashMap hashMap = new HashMap();
                        hashMap.put("hoten",hoten);
                        hashMap.put("ngaysinh",ngaysinh);
                        hashMap.put("sdt",sdt);
                        hashMap.put("image",uri.toString());
                        hashMap.put("status","offline");
                        hashMap.put("onlineStatus","online");
                        hashMap.put("uid",mAuth.getUid());

                        mRef.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(o -> {
                            loadingBar.dismiss();
                            Toast.makeText(SetupActivity.this, "Đăng ký tài khoản thành công! Vui lòng xác thực địa chỉ email để đăng nhập tài khoản!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SetupActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(e -> {
                            loadingBar.dismiss();
                            Toast.makeText(SetupActivity.this, "Đăng ký tài khoản thất bại!", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    });
                }
            });
        }
    }

    private void showError(EditText text, String s) {
        text.setError(s);
        text.requestFocus();
    }

    private void KhoiTao() {
        txtHoTen = findViewById(R.id.txtHoTen);
        txtNgaySinh = findViewById(R.id.txtNgaySinh);
        txtSDT = findViewById(R.id.txtSDT);
        btnXacNhanTT = findViewById(R.id.btnXacNhanTT);
        btnChonNgay = findViewById(R.id.btnChonNgay);
        circleImageView = findViewById(R.id.profile_image);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfileImage");
        loadingBar = new ProgressDialog(this);
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