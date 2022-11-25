package com.example.appchat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.Entity.Chat;
import com.example.appchat.Entity.Message;
import com.example.appchat.Entity.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {

    private TextView tvChat;
    private Button logout;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mUserRef,chatRef;
    private String imageProfileV,userNameV,myId;
    private FloatingActionButton floatingActionButton;
    private RecyclerView chatList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        floatingActionButton = view.findViewById(R.id.floatButton);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        myId = mUser.getUid();
        chatList = view.findViewById(R.id.view_message);
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRef = FirebaseDatabase.getInstance().getReference().child("Chats").child(myId);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),SearchFriendActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Chat> options = new FirebaseRecyclerOptions.Builder<Chat>()
                .setQuery(chatRef, Chat.class)
                .build();

        FirebaseRecyclerAdapter<Chat,MessageList> adapter = new FirebaseRecyclerAdapter<Chat,MessageList>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageList holder, int position, @NonNull Chat model) {
                int pos = position;
                String userId = getRef(pos).getKey();
//                chatRef.orderByChild("sender").equalTo(myId).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Toast.makeText(getContext(), "Tim thay", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
                mUserRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String img = snapshot.child("image").getValue().toString();
                        String username = snapshot.child("hoten").getValue().toString();
                        holder.userNameChatTv.setText(username);
                        Picasso.get().load(img).into(holder.circle_listChat);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                chatRef.child(userId).getRef().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            Message message = ds.getValue(Message.class);

                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(message.getTimestamp()));
                            String datetime = DateFormat.format("hh:mm aa",cal).toString();
                            holder.timestampChatTv.setText(datetime);
                            holder.messageChatTv.setText(message.getMessage());
                        }
                        //Toast.makeText(getContext(), ""+message.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),ChatActivity.class);
                        intent.putExtra("yourUid",getRef(pos).getKey());
                        startActivity(intent);
                    }
                });
            }
            @NonNull
            @Override
            public MessageList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chat_message,parent,false);
                return new MessageList(view);
            }
        };
        chatList.setAdapter(adapter);
        adapter.startListening();
    }
    public static class MessageList extends RecyclerView.ViewHolder{

        TextView userNameChatTv,messageChatTv,timestampChatTv;
        CircleImageView circle_listChat;
        public MessageList(@NonNull View itemView) {
            super(itemView);
            userNameChatTv = itemView.findViewById(R.id.userNameChatTv);
            messageChatTv = itemView.findViewById(R.id.messageChatTv);
            timestampChatTv = itemView.findViewById(R.id.timestampChatTv);
            circle_listChat = itemView.findViewById(R.id.circle_listChat);
        }
    }
}