package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.appchat.Entity.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchFriendActivity extends AppCompatActivity {

    private ImageButton imgSearch;
    private RecyclerView recyclerViewSearchFriend;
    private Button btnTaoGroup;
    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        imgSearch = findViewById(R.id.img_back);
        mUserRef = FirebaseDatabase.getInstance().getReference("Users");
        recyclerViewSearchFriend = findViewById(R.id.recyclerViewSearchFriend);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnTaoGroup = findViewById(R.id.btn_TaoMoiGroup);
        btnTaoGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFriendActivity.this,GroupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(mUserRef,User.class).build();
        FirebaseRecyclerAdapter<User, SearchFriendActivity.FindFriendViewHolder> adapter = new FirebaseRecyclerAdapter<User, SearchFriendActivity.FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SearchFriendActivity.FindFriendViewHolder holder, int position, @NonNull User model) {
                int pos = position;
                holder.txtUserName1.setText(model.getHoten());
                Picasso.get().load(model.getImage()).into(holder.circleImageView1);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String yourId = getRef(pos).getKey();
                        Intent intent = new Intent(SearchFriendActivity.this,FriendProfile.class);
                        intent.putExtra("keyValue",yourId);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public SearchFriendActivity.FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_friend,parent,false);
                return new SearchFriendActivity.FindFriendViewHolder(view);
            }
        };
        recyclerViewSearchFriend.setAdapter(adapter);
        adapter.startListening();
    }
    public static class FindFriendViewHolder extends RecyclerView.ViewHolder{

        TextView txtUserName1;
        CircleImageView circleImageView1;
        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName1 = itemView.findViewById(R.id.txtUserName1);
            circleImageView1 = itemView.findViewById(R.id.circleImageView1);
        }
    }
}