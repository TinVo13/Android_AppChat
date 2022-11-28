package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.Entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatInfoActivity extends AppCompatActivity {
    private ImageButton btnBackChatInfo;
    private TextView xoaBanTv,timTinNhantv;
    private CircleImageView circleImageView;
    private TextView textView,textViewStatus,textViewAddGroup;
    String yourId = "";
    String mId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);

        khoitao();

        btnBackChatInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        xoaBanTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatInfoActivity.this)
                        .setCancelable(true)
                        .setTitle("Bạn có chắc muốn xóa bạn với người này?")
                        .setIcon(R.drawable.ic_warning)
                        .setMessage("Nguời này không biết đâu :))")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RemoveContacts();
                            }
                        })
                        .setNegativeButton("Thôi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();
            }
        });
        getInfo();
    }

    private void khoitao() {
        btnBackChatInfo = findViewById(R.id.btnBackChatInfo);
        xoaBanTv = findViewById(R.id.xoaBanTv);
        timTinNhantv = findViewById(R.id.timTinNhanTv);
        textView = findViewById(R.id.chatInfoHoTen);
        textViewStatus = findViewById(R.id.tv_status);
        textViewAddGroup = findViewById(R.id.textViewAddGroup);
        circleImageView = findViewById(R.id.chatInfoImg);
        yourId = getIntent().getStringExtra("keyValue");
        mId = FirebaseAuth.getInstance().getUid();
    }
    private void getInfo(){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.child(yourId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    //Toast.makeText(ChatInfoActivity.this, "ajdsa"+user.getHoten(), Toast.LENGTH_SHORT).show();
                    Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(circleImageView);
                    textView.setText(user.getHoten());
                    textViewStatus.setText("'"+user.getStatus()+"'");
                }
                //Toast.makeText(ChatInfoActivity.this, "khong thay", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatInfoActivity.this, "Khong tim thay", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void RemoveContacts() {
        DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        contactRef.child(mId).child(yourId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactRef.child(yourId).child(mId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                xoaBanTv.setText("Kết bạn");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}