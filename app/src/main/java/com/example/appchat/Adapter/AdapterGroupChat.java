package com.example.appchat.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.Entity.GroupChat;
import com.example.appchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.HolderGroupChat> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private ArrayList<GroupChat> groupChatList;
    private FirebaseAuth auth;

    public AdapterGroupChat(Context context, ArrayList<GroupChat> groupChatList) {
        this.context = context;
        this.groupChatList = groupChatList;
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderGroupChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflater giao dien
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.view_groupchat_right,parent,false);
            return new HolderGroupChat(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.view_groupchat_left,parent,false);
            return new HolderGroupChat(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChat holder, int position) {
        //get data
        GroupChat groupChat = groupChatList.get(position);
        String timestamp = groupChat.getTimestamp();
        String message = groupChat.getMessage();
        String senderUid = groupChat.getSender();
        //chuyen doi timestamp thanh thoi gian dd/mm/yyyy
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();

        //set data
        holder.messageTV.setText(message);
        holder.timestampTV.setText(datetime);
        setUserName(groupChat,holder);
    }

    private void setUserName(GroupChat groupChat, HolderGroupChat holder) {
        //lay du lieu nguoi gui tu uid trong groupchat
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(groupChat.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String name = ""+ds.child("hoten").getValue();
                            holder.nameTV.setText(name);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Tim khong thay", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemViewType(int position) {
        if(groupChatList.get(position).getSender().equals(auth.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return groupChatList.size();
    }

    class HolderGroupChat extends RecyclerView.ViewHolder{
        private TextView nameTV,messageTV,timestampTV;

        public HolderGroupChat(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.nameTV);
            messageTV = itemView.findViewById(R.id.messageTV);
            timestampTV = itemView.findViewById(R.id.timestampTV);
        }
    }
}
