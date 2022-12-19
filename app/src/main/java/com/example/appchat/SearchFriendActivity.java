package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.appchat.Adapter.AdapterUser;
import com.example.appchat.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class SearchFriendActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSearchFriend;
    private DatabaseReference mUserRef;
    private FirebaseAuth auth;
    private List<User> listUser;
    private AdapterUser adapterUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        ImageButton imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(v -> finish());
        auth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference("Users");
        recyclerViewSearchFriend = findViewById(R.id.recyclerViewSearchFriend);
        Button btnTaoGroup = findViewById(R.id.btn_TaoMoiGroup);
        btnTaoGroup.setOnClickListener(v -> {
            Intent intent = new Intent(SearchFriendActivity.this,GroupActivity.class);
            startActivity(intent);
            finish();
        });
        EditText etSearch = findViewById(R.id.txtSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //searchUser("");
        getListUser();
    }
    private void searchUser(String s){
        List<User> list = new ArrayList<>();
        Query query = mUserRef.child("username").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        list.add(ds.getValue(User.class));
                    }
                    AdapterUser adapterUser = new AdapterUser(SearchFriendActivity.this,list);
                    recyclerViewSearchFriend.setAdapter(adapterUser);
                    adapterUser.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchFriendActivity.this, "Không có dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getListUser() {
        String currentUser = auth.getCurrentUser().getUid();
        listUser = new ArrayList<>();
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds: snapshot.getChildren()){
                        String id = ds.getKey();
                        if(id!=null) {
                            mUserRef.child(id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    //if (snapshot.exists()){
                                    User user = snapshot.getValue(User.class);
                                    listUser.add(user);
                                    adapterUser = new AdapterUser(SearchFriendActivity.this,listUser);
                                    recyclerViewSearchFriend.setAdapter(adapterUser);
                                    adapterUser.notifyDataSetChanged();
                                    //}
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(SearchFriendActivity.this, "Loi "+error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchFriendActivity.this, "Loi "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(mUserRef,User.class).build();
//        FirebaseRecyclerAdapter<User, SearchFriendActivity.FindFriendViewHolder> adapter = new FirebaseRecyclerAdapter<User, SearchFriendActivity.FindFriendViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull SearchFriendActivity.FindFriendViewHolder holder, int position, @NonNull User model) {
//                int pos = position;
//                holder.txtUserName1.setText(model.getHoten());
//                Picasso.get().load(model.getImage()).into(holder.circleImageView1);
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String yourId = getRef(pos).getKey();
//                        Intent intent = new Intent(SearchFriendActivity.this,FriendProfile.class);
//                        intent.putExtra("keyValue",yourId);
//                        startActivity(intent);
//                    }
//                });
//            }
//
//            @NonNull
//            @Override
//            public SearchFriendActivity.FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_friend,parent,false);
//                return new SearchFriendActivity.FindFriendViewHolder(view);
//            }
//        };
//        recyclerViewSearchFriend.setAdapter(adapter);
//        adapter.startListening();
//    }
//    public static class FindFriendViewHolder extends RecyclerView.ViewHolder{
//
//        TextView txtUserName1;
//        CircleImageView circleImageView1;
//        public FindFriendViewHolder(@NonNull View itemView) {
//            super(itemView);
//            txtUserName1 = itemView.findViewById(R.id.txtUserName1);
//            circleImageView1 = itemView.findViewById(R.id.circleImageView1);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adapterUser != null){
            AdapterUser.release();
        }
    }
}