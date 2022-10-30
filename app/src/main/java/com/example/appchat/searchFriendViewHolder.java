package com.example.appchat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class searchFriendViewHolder extends RecyclerView.ViewHolder {
    CircleImageView circleImageView;
    TextView tvUserName;
    public searchFriendViewHolder(@NonNull View itemView) {
        super(itemView);
        circleImageView = itemView.findViewById(R.id.circleImageView1);
        tvUserName = itemView.findViewById(R.id.txtUserName1);
    }
}
