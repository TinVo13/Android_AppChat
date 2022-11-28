package com.example.appchat.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.Entity.User;
import com.example.appchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterDenieRequestFriend extends RecyclerView.Adapter<AdapterDenieRequestFriend.DenieViewHolder>{
    private List<User> listUser;
    private FirebaseAuth auth;
    private DatabaseReference userRef,contactRef,requestRef;
    String currentUserId = "";

    public AdapterDenieRequestFriend(List<User> listUser) {
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public DenieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_denie_request_friend,parent,false);
        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        currentUserId = auth.getCurrentUser().getUid();
        return new DenieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DenieViewHolder holder, int position) {
        User user = listUser.get(position);
        if (user == null){
            return;
        }
        holder.textViewName.setText(user.getHoten());
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.circleImageView);
        holder.btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public static class DenieViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView textViewName;
        Button btnHuy;
        public DenieViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.circleDenie);
            textViewName = itemView.findViewById(R.id.textViewNameDenie);
            btnHuy = itemView.findViewById(R.id.btnDenie);
        }
    }
}
