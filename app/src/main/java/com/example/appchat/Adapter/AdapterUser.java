package com.example.appchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.Entity.User;
import com.example.appchat.FriendProfile;
import com.example.appchat.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.UserViewHolder>{
    List<User> list;
    static Context context;

    public AdapterUser(Context context,List<User> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_friend,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = list.get(position);
        if (user != null){
            holder.textView.setText(user.getHoten());
            Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.circleImageView);

            holder.itemView.setOnClickListener(v->{
                Intent intent = new Intent(context, FriendProfile.class);
                intent.putExtra("keyValue",user.getUid());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (list==null){
            return 0;
        }
        return list.size();
    }
    public static void release(){
        context = null;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView textView;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txtUserName1);
            circleImageView = itemView.findViewById(R.id.circleImageView1);
        }
    }
}
