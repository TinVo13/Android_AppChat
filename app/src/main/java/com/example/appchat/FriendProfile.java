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
    private DatabaseReference userRef,requestRef,contactRef;
    private String imageUrl,userName,yourId,myId;
    private CircleImageView circleImageView;
    private TextView tvUserName;
    private Button btnKetBan,btnXoa;
    private String currentState = "new";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        yourId = getIntent().getStringExtra("keyValue");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(yourId);
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        auth = FirebaseAuth.getInstance();
        myId = auth.getCurrentUser().getUid();
        circleImageView = findViewById(R.id.circleImageView2);
        tvUserName = findViewById(R.id.tvUserNameFriend);
        btnKetBan = findViewById(R.id.btnKetBan);
        btnXoa = findViewById(R.id.btnXoa);
        LoadUser();

        btnKetBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendFriendRequest();
            }
        });
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
                    
                    GuiKetBan();
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

    private void GuiKetBan() {
        requestRef.child(myId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(yourId)){
                            String request_type = snapshot.child(yourId).child("request_type").getValue().toString();
                            if(request_type.equals("sent")){
                                currentState = "request_sent";
                                btnKetBan.setText("Hủy lời mời kết bạn");
                            }
                            else if(request_type.equals("received")){
                                currentState = "request_received";
                                btnKetBan.setText("Chấp nhận kết bạn");
                                btnXoa.setVisibility(View.VISIBLE);
                                btnXoa.setEnabled(true);
                                btnXoa.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelFriendRequest();
                                    }
                                });
                            }
                        }
                        else {
                            contactRef.child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild(yourId)){
                                        currentState = "friends";
                                        btnKetBan.setText("Xoá bạn");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        if(!(myId.equals(yourId))){
            btnKetBan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentState.equals("new")){
                        SendFriendRequest();
                    }
                    if(currentState.equals("request_sent")){
                        CancelFriendRequest();
                    }
                    if(currentState.equals("request_received")){
                        AcceptFriend();
                    }if (currentState.equals("friends")){
                        RemoveContacts();
                    }
                }
            });
        }else{
            btnKetBan.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveContacts() {
        contactRef.child(myId).child(yourId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactRef.child(yourId).child(myId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                btnKetBan.setEnabled(true);
                                                currentState = "new";
                                                btnKetBan.setText("Kết bạn");
                                                btnXoa.setVisibility(View.INVISIBLE);
                                                btnXoa.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriend() {
        contactRef.child(myId).child(yourId)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactRef.child(yourId).child(myId)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                requestRef.child(myId).child(yourId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    requestRef.child(yourId).child(myId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    btnKetBan.setEnabled(true);
                                                                                    currentState = "friends";
                                                                                    btnKetBan.setText("Xóa bạn");
                                                                                    btnXoa.setVisibility(View.INVISIBLE);
                                                                                    btnXoa.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelFriendRequest() {
        requestRef.child(myId).child(yourId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            requestRef.child(yourId).child(myId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                btnKetBan.setEnabled(true);
                                                currentState = "new";
                                                btnKetBan.setText("Kết bạn");
                                                btnXoa.setVisibility(View.INVISIBLE);
                                                btnXoa.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendFriendRequest() {
        requestRef.child(myId).child(yourId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            requestRef.child(yourId).child(myId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                btnKetBan.setEnabled(true);
                                                currentState = "request_sent";
                                                btnKetBan.setText("Hủy lời mời kết bạn!");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}