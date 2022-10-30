package com.example.appchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appchat.Entity.Group;
import com.example.appchat.GroupChatActivity;
import com.example.appchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterGroupChatList extends RecyclerView.Adapter<AdapterGroupChatList.holderGroupchatList> {
    private Context context;
    private ArrayList<Group> groupChatLists;

    public AdapterGroupChatList(Context context, ArrayList<Group> groupChatLists) {
        this.context = context;
        this.groupChatLists = groupChatLists;
    }

    @NonNull
    @Override
    public holderGroupchatList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //hien thi giao dien
        View view = LayoutInflater.from(context).inflate(R.layout.view_groupchat_list,parent,false);
        return new holderGroupchatList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holderGroupchatList holder, int position) {
        //lay du lieu
        Group model = groupChatLists.get(position);
        String groupId = model.getGroupId();
        String groupImg = model.getGroupImg();
        String groupTitle = model.getGroupTitle();
        holder.nameTv.setText("");
        holder.messageTv.setText("");
        holder.timeTv.setText("");
        //load last message and timestamp
        loadLastMessage(model,holder);
        //set data
        holder.groupTitleTv.setText(groupTitle);
        try{
            Picasso.get().load(groupImg).placeholder(R.drawable.ic_group).into(holder.groupImg);
        }catch(Exception e){
            holder.groupImg.setImageResource(R.drawable.ic_group);
        }
        //handle click vao nhom
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupId",groupId);
                context.startActivity(intent);
            }
        });
    }

    private void loadLastMessage(Group model, holderGroupchatList holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(model.getGroupId()).child("Messages").limitToLast(1)//lay tin nhan cuoi cung
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String message = ""+ds.child("message").getValue();
                            String timestamp = ""+ds.child("timestamp").getValue();
                            String sender = ""+ds.child("sender").getValue();
                            //chuyen doi timestamp thanh thoi gian dd/mm/yyyy
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(timestamp));
                            String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
                            holder.messageTv.setText(message);
                            holder.timeTv.setText(datetime);
                            //lay thong tin nguoi gui cuoi cung

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                            reference.orderByChild("uid").equalTo(sender)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot ds: snapshot.getChildren()){
                                                String name = ""+snapshot.child("hoten").getValue();
                                                holder.nameTv.setText(name);
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
    }

    @Override
    public int getItemCount() {
        return groupChatLists.size();
    }

    class holderGroupchatList extends RecyclerView.ViewHolder{

        private CircleImageView groupImg;
        private TextView groupTitleTv,nameTv,messageTv,timeTv;

        public holderGroupchatList(@NonNull View itemView) {
            super(itemView);
            groupImg = itemView.findViewById(R.id.groupImg);
            groupTitleTv = itemView.findViewById(R.id.groupTitleTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);

        }
    }
}
