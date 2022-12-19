package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.Adapter.AdapterChat;
import com.example.appchat.Entity.Chat;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 108;
    private TextView chatNameTv,chatStatusTv;
    private ImageButton chatSend,chatSendFile,chatInfo,chatBack;
    private RecyclerView chatRecyclerView;
    private CircleImageView chatImg;
    private EditText chatMessage;
    private FirebaseAuth auth;
    private String uid;
    private String yourUid;
    private String yourImage;
    private ValueEventListener seenListener;
    private List<Chat> chatList;
    private AdapterChat adapterChat;
    private DatabaseReference refSeen,chatRef;
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //init component
        initComponent();
        //friend info
        chatInfo.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this,ChatInfoActivity.class);
            intent.putExtra("keyValue",yourUid);
            startActivity(intent);
            //finish();
        });
        chatSend.setOnClickListener(v -> {
            String message = chatMessage.getText().toString().trim();
            if(TextUtils.isEmpty(message)){
                Toast.makeText(ChatActivity.this, "Khong the de trong tin nhan", Toast.LENGTH_SHORT).show();
            }else{
                sendMessage(message);
            }
        });
        chatSendFile.setOnClickListener(v->{
            String[] listtacvu = {"Hình ảnh & Video","File"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Chọn 1 tác vụ!")
                    .setItems(listtacvu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:{
                                    showImagePickDialog();
                                    break;
                                }
                                case 1:{
                                    Toast.makeText(ChatActivity.this, "Chon 2", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        }
                    }).show();
        });
        chatBack.setOnClickListener(nv->{
            finish();
        });
        //check status
        checkUserStatus();
        //search user
        searchUser();

        readMessage();
        seenMessage(yourUid);
    }

    private void showImagePickDialog() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CODE);
    }

    private void searchUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(yourUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    //get value
                    if (ds.exists()){
                        String nametv = ""+ds.child("hoten").getValue();
                        yourImage = ""+ds.child("image").getValue();
                        String status = ""+ds.child("onlineStatus").getValue();
                        if(status.equals("online")){
                            chatStatusTv.setText(status);
                        }else{
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(status));
                            String datetime = DateFormat.format("hh:mm aa",cal).toString();
                            chatStatusTv.setText("Truy cập lần cuối: "+datetime);
                        }
                        //set value
                        chatNameTv.setText(nametv);
                        try {
                            Picasso.get().load(yourImage).placeholder(R.drawable.profile).into(chatImg);
                        }catch(Exception e){
                            Picasso.get().load(R.drawable.profile).into(chatImg);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenMessage(String userId) {
        boolean seen = true;
        refSeen = FirebaseDatabase.getInstance().getReference().child("Chats").child(uid).child(userId);
        seenListener = refSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    //Toast.makeText(ChatActivity.this, ""+chat.getSeen(), Toast.LENGTH_SHORT).show();
                    if(chat.getReceiver().equals(uid)&&chat.getSender().equals(userId)==true){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen",true);
                        ds.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage() {
        chatList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        reference.child(uid).child(yourUid).getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if (chat.getReceiver().equals(auth.getUid())&&chat.getSender().equals(yourUid)||
                    chat.getReceiver().equals(yourUid)&&chat.getSender().equals(uid)){
                        chatList.add(chat);
                    }
                    //adapter
                    adapterChat = new AdapterChat(ChatActivity.this,chatList,yourImage);
                    adapterChat.notifyDataSetChanged();
                    //set adapter
                    chatRecyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String message) {
        chatRef = FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",auth.getUid());
        hashMap.put("receiver",yourUid);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isSeen",false);
        hashMap.put("type","text");
        chatRef.child("Chats").child(uid).child(yourUid).push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    chatRef.child("Chats").child(yourUid).child(uid).push().setValue(hashMap);
                }
            }
        });
        chatMessage.setText("");
    }
    private void sendImageMessage(Uri imageUri) throws IOException {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đang gửi hình ảnh!");
        progressDialog.setMessage("Vui lòng chờ vài giây...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String timestamp = ""+System.currentTimeMillis();
        String path = "ChatImages/"+"_post"+timestamp;
        //get bitmap from image uri
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(path);
        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //upload image
                progressDialog.dismiss();
                //get url
                Task<Uri> taskUri = taskSnapshot.getStorage().getDownloadUrl();
                while (!taskUri.isSuccessful());
                String downloadUri = taskUri.getResult().toString();

                if(taskUri.isSuccessful()){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("sender",uid);
                    hashMap.put("receiver",yourUid);
                    hashMap.put("message",downloadUri);
                    hashMap.put("timestamp",timestamp);
                    hashMap.put("isSeen",false);
                    hashMap.put("type","image");

                    reference.child("Chats").child(uid).child(yourUid).push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                reference.child("Chats").child(yourUid).child(uid).push().setValue(hashMap);
                            }
                        }
                    });

                    //gui thong bao
//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid);
//                    ref.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = auth.getCurrentUser();
        if (user!=null){
            uid = user.getUid();
            yourUid = getIntent().getStringExtra("yourUid");
            //Toast.makeText(this, ""+yourUid, Toast.LENGTH_SHORT).show();
        }else{
            startActivity(new Intent(this,MainActivity.class));
        }
    }
    private void checkOnlineStatus(String status){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(auth.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus",status);
        //update status
        ref.updateChildren(hashMap);
    }

    private void initComponent() {
        chatNameTv = findViewById(R.id.chatNameTv);
        chatStatusTv = findViewById(R.id.chatStatusTv);
        chatSend = findViewById(R.id.chatSend);
        chatSendFile = findViewById(R.id.chatBtn);
        chatInfo = findViewById(R.id.chatInfo);
        chatBack = findViewById(R.id.btnBackChat);
        chatMessage = findViewById(R.id.chatMessage);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatImg = findViewById(R.id.chatImg);
        auth = FirebaseAuth.getInstance();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOnlineStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //get timestamp
        String time = String.valueOf(System.currentTimeMillis());
        //set offline status
        checkOnlineStatus(time);
        refSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE&&resultCode==RESULT_OK&&data!=null){
            imageUri = data.getData();
            try {
                sendImageMessage(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}