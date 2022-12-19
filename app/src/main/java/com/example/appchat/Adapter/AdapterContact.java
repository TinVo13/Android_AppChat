package com.example.appchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.ChatActivity;
import com.example.appchat.Entity.User;
import com.example.appchat.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterContact extends RecyclerView.Adapter<AdapterContact.ContactViewHolder>{
    private List<User> listUser;
    private static Context context;

    public AdapterContact(List<User> listUser, Context context) {
        this.listUser = listUser;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_friend,parent,false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        int pos = position;
        User user = listUser.get(position);
        if (user == null){
            return;
        }
        holder.txtUserName1.setText(user.getHoten());
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.circleImageView1);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("yourUid",user.getUid());
            context.startActivity(intent);
        });
    }
    public static void release(){
        context = null;
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName1;
        CircleImageView circleImageView1;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName1 = itemView.findViewById(R.id.txtUserName1);
            circleImageView1 = itemView.findViewById(R.id.circleImageView1);
        }
    }
}
