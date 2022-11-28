package com.example.appchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.Entity.User;
import com.example.appchat.R;
import com.example.appchat.RequestActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRequestFriend extends RecyclerView.Adapter<AdapterRequestFriend.RequestViewHolder> {
    List<User> listUser;
    Context context;
    FirebaseAuth auth;
    DatabaseReference userRef,contactRef,requestRef;
    String currentUserId;

    public AdapterRequestFriend(List<User> listUser, Context context) {
        this.listUser = listUser;
        this.context = context;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_request_friend,parent,false);
        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        currentUserId = auth.getCurrentUser().getUid();
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        User user = listUser.get(position);
        if (user==null){
            return;
        }
        holder.tv_Name.setText(user.getHoten());
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.circleImageView);
        holder.btn_chapNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = user.getUid();
                AcceptFriend(key);
            }
        });
        holder.btn_tuChoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView tv_Name;
        Button btn_chapNhan,btn_tuChoi;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.circleImageViewRequest);
            tv_Name = itemView.findViewById(R.id.nameTvRequest);
            btn_chapNhan = itemView.findViewById(R.id.btnAccept);
            btn_tuChoi = itemView.findViewById(R.id.btnDenie);
            btn_chapNhan.setVisibility(View.VISIBLE);
            btn_tuChoi.setVisibility(View.VISIBLE);
        }
    }
    private void AcceptFriend(String yourId) {
        listUser.clear();
        contactRef.child(currentUserId).child(yourId)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactRef.child(yourId).child(currentUserId)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                requestRef.child(currentUserId).child(yourId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    requestRef.child(yourId).child(currentUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    Toast.makeText(context, "Thêm bạn thành công", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
