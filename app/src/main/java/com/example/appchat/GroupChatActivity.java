package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.Adapter.AdapterGroupChat;
import com.example.appchat.Entity.GroupChat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {
    private String groupId;
    private Toolbar toolbar;
    private TextView groupTitleGroup;
    private CircleImageView groupIconTv;
    private ImageButton imgButton,sendBtn;
    private EditText messageTv;
    private RecyclerView chatRv;
    private FirebaseAuth auth;
    private ArrayList<GroupChat> groupChatsList;
    private AdapterGroupChat adapterGroupChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        //lay du lieu
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        initComponent();
        loadInforGroup();

        loadGroupMessage();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageTv.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(GroupChatActivity.this, "Khong the gui tin nhan rong", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(message);
                }
            }
        });
    }

    private void loadGroupMessage() {
        //init list
        groupChatsList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        groupChatsList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            GroupChat model = ds.getValue(GroupChat.class);
                            groupChatsList.add(model);
                        }
                        adapterGroupChat = new AdapterGroupChat(GroupChatActivity.this,groupChatsList);
                        chatRv.setAdapter(adapterGroupChat);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sendMessage(String mes) {
        String ts = ""+System.currentTimeMillis();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",""+auth.getUid());
        hashMap.put("message",mes);
        hashMap.put("timestamp",""+ts);
        hashMap.put("type",""+"text");//text/img/file

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Messages").child(ts).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //gui tin nhan thanh cong
                        //clear message
                        messageTv.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //gui tin nhan that bai
                        Toast.makeText(GroupChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadInforGroup() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()) {
                            String groupTitle = ds.child("groupTitle").getValue().toString();
                            String groupDes = ds.child("groupDes").getValue().toString();
                            String groupImg = ds.child("groupImg").getValue().toString();
                            String timestamp = ds.child("timestamp").getValue().toString();
                            String createBy = ds.child("createBy").getValue().toString();

                            groupTitleGroup.setText(groupTitle);
                            try {
                                Picasso.get().load(groupImg).placeholder(R.drawable.ic_group_white).into(groupIconTv);
                            }catch(Exception e){
                                groupIconTv.setImageResource(R.drawable.ic_group_white);
                            }

                        }
                        if(snapshot.exists()){

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void initComponent() {
        toolbar = findViewById(R.id.toolbar);
        groupIconTv = findViewById(R.id.groupIconTv);
        groupTitleGroup = findViewById(R.id.groupTitleGroup);
        imgButton = findViewById(R.id.imgButton);
        sendBtn = findViewById(R.id.sendBtn);
        messageTv = findViewById(R.id.messageTv);
        auth = FirebaseAuth.getInstance();
        chatRv = findViewById(R.id.chatRv);
    }
}