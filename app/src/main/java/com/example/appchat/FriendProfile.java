package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfile extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference userRef,requestRef,friendRef;
    private FirebaseUser mUser;
    private String imageUrl,userName;
    private CircleImageView circleImageView;
    private TextView tvUserName;
    private Button btnKetBan,btnXoa;
    private String currentState = "nothing_happen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        String userId = getIntent().getStringExtra("keyValue");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        circleImageView = findViewById(R.id.circleImageView2);
        tvUserName = findViewById(R.id.tvUserNameFriend);
        btnKetBan = findViewById(R.id.btnKetBan);
        btnXoa = findViewById(R.id.btnXoa);
        LoadUser();

        btnKetBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KetBan(userId);
            }
        });
        CheckUserExitstance(userId);
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                huyKetBan(userId);
            }
        });
        CheckUserExitstance(userId);
    }

    private void huyKetBan(String id) {
        if(currentState.equals("friend")){
            friendRef.child(mUser.getUid()).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        friendRef.child(id).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(FriendProfile.this, "Đã hủy kết bạn4", Toast.LENGTH_SHORT).show();
                                currentState = "nothing_happen";
                                btnKetBan.setText("Kết bạn");
                                btnXoa.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        }
        if(currentState.equals("he_sent_pending")){
            HashMap hashMap = new HashMap();
            hashMap.put("status","decline");
            requestRef.child(id).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Toast.makeText(FriendProfile.this, "Bạn đã từ chối yêu cầu kết bạn3", Toast.LENGTH_SHORT).show();
                    currentState = "he_sent_decline";
                    btnKetBan.setVisibility(View.VISIBLE);
                    btnXoa.setVisibility(View.GONE);
                }
            });
        }
    }

    private void CheckUserExitstance(String id) {
        friendRef.child(mUser.getUid()).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    currentState = "friend";
                    btnKetBan.setText("Gửi tin nhắn");
                    btnXoa.setText("Hủy kết bạn");
                    btnXoa.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        friendRef.child(id).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    currentState = "friend";
                    btnKetBan.setText("Gửi tin nhắn");
                    btnXoa.setText("Hủy kết bạn");
                    btnXoa.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(mUser.getUid()).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("status").getValue().toString().equals("pending")){
                        currentState = "i_sent_pending";
                        btnKetBan.setText("Hủy yêu cầu kết bạn");
                        btnXoa.setVisibility(View.GONE);
                    }if(snapshot.child("status").getValue().toString().equals("decline")){
                        currentState = "i_sent_decline";
                        btnKetBan.setText("Hủy yêu cầu kết bạn");
                        btnXoa.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(id).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("status").getValue().toString().equals("pending")){
                        currentState = "he_sent_pending";
                        btnKetBan.setText("Chấp nhận");
                        btnXoa.setText("Từ chối");
                        btnXoa.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(currentState.equals("nothing_happen")){
            currentState = "nothing_happen";
            btnKetBan.setText("Kết bạn");
            btnXoa.setVisibility(View.GONE);
        }
    }

    private void KetBan(String Id) {
        if(currentState.equals("nothing_happen")){
            HashMap hashMap = new HashMap();
            hashMap.put("status","pending");
            requestRef.child(Id).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
//                        Toast.makeText(FriendProfile.this, "Bạn đã gửi lời mời kết bạn2!", Toast.LENGTH_SHORT).show();
                        btnXoa.setVisibility(View.GONE);
                        currentState = "i_sent_pending";
                        btnKetBan.setText("Hủy yêu cầu kết bạn");

                    }else{
                        Toast.makeText(FriendProfile.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(currentState.equals("i_sent_pending")||currentState.equals("i_sent_decline")){
            requestRef.child(Id).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
//                        Toast.makeText(FriendProfile.this, "Bạn đã hủy lời mời kết bạn1!", Toast.LENGTH_SHORT).show();
                        currentState = "nothing_happen";
                        btnKetBan.setText("Kết bạn");
                        btnXoa.setVisibility(View.GONE);
                    }else{
                        Toast.makeText(FriendProfile.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(currentState.equals("he_sent_pending")){
            requestRef.child(mUser.getUid()).child(Id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            requestRef.child(mUser.getUid()).child(Id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        HashMap hashMap = new HashMap();
                        hashMap.put("status","friend");
                        hashMap.put("hoten",userName);
                        hashMap.put("image",imageUrl);
                        friendRef.child(Id).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    friendRef.child(mUser.getUid()).child(Id).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
//                                            Toast.makeText(FriendProfile.this, "Kets bạn thành công!", Toast.LENGTH_SHORT).show();
                                            currentState = "friend";
                                            btnKetBan.setText("Gửi tin nhắn");
                                            btnXoa.setText("Hủy kết bạn");
                                            btnXoa.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
            requestRef.child(mUser.getUid()).child(Id).removeValue();
        }
        if(currentState.equals("friend")){
            //khi da ket ban
            Intent intent = new Intent(FriendProfile.this,ChatActivity.class);
            intent.putExtra("yourUid",Id);
            startActivity(intent);
        }
    }

    private void LoadUser() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    imageUrl = snapshot.child("image").getValue().toString();
                    userName = snapshot.child("hoten").getValue().toString();

                    Picasso.get().load(imageUrl).into(circleImageView);
                    tvUserName.setText(userName);
                }else{
                    Toast.makeText(FriendProfile.this, "Không tìm thấy dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FriendProfile.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}