package com.example.appchat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.Entity.Chat;
import com.example.appchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<Chat> chatList;
    String imageUrl;
    FirebaseAuth auth;

    public AdapterChat(Context context, List<Chat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //tao giao dien
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.view_chat_right, parent, false);
            return new MyHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.view_chat_left, parent, false);
            return new MyHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder,int position) {
        int pos = position;
        //get data
        Chat chat = chatList.get(position);
        String message = chat.getMessage();
        String timestamp = chat.getTimestamp();

        //convert to time
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String datetime = DateFormat.format("hh:mm aa",cal).toString();
        //set data
        holder.message.setText(message);
        holder.timestamp.setText(datetime);
        try {
            Picasso.get().load(imageUrl).into(holder.imgChat);
        }catch (Exception e){
            Picasso.get().load(imageUrl).placeholder(R.drawable.profile).into(holder.imgChat);
        }
        //click to show delete dialog
        holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xóa tin nhắn");
                builder.setMessage("Bạn có chắc muốn xóa tin nhắn này? Điều này sẽ xóa tin nhắn vĩnh viễn!");
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete message
                        String msgTimestamp = chatList.get(pos).getTimestamp();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
                        Query query = ref.child(chat.getSender()).child(chat.getReceiver()).orderByChild("timestamp").equalTo(msgTimestamp);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    if(ds.child("sender").getValue().equals(auth.getUid())){
                                        //remove the message
                                        //ds.getRef().removeValue();
                                        //set message when delete
                                        HashMap<String,Object> hashMap = new HashMap<>();
                                        hashMap.put("message","Tin nhắn đã xóa...");
                                        ds.getRef().updateChildren(hashMap);
                                    }
                                    else{
                                        Toast.makeText(context, "Bạn không thể xóa tin nhắn của người khác!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                //cancel
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });
        //set seen status
        if(position == chatList.size()-1){
            if(chatList.get(position).isSeen()){
                holder.isSeen.setText("Seen");
            }else{
                holder.isSeen.setText("Delivered");
            }
        }else{
            holder.isSeen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(chatList.get(position).getSender().equals(auth.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{
        CircleImageView imgChat;
        TextView timestamp,isSeen,message;
        RelativeLayout messageLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imgChat = itemView.findViewById(R.id.imgChat1);
            timestamp = itemView.findViewById(R.id.timestampTv1);
            isSeen = itemView.findViewById(R.id.seenChat1);
            message = itemView.findViewById(R.id.messageChat1);
            messageLayout = itemView.findViewById(R.id.messageLayout);

        }
    }
}
