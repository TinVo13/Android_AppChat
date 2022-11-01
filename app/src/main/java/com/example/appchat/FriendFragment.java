package com.example.appchat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.Entity.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendFragment extends Fragment {

    private FirebaseRecyclerOptions<User> options;
    private FirebaseRecyclerAdapter<User,searchFriendViewHolder> adapter;
    private DatabaseReference mUserRef,contactRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private RecyclerView recyclerView;
    private EditText txtSearch;
    private ImageButton btnSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewFriend);
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(mUser.getUid());
        txtSearch = view.findViewById(R.id.txtSearch);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //LoadUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }
    private void LoadUser(String s) {
        Query query = mUserRef.orderByChild("hoten").startAt(s).endAt(s+"\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query,User.class).build();
        adapter = new FirebaseRecyclerAdapter<User, searchFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull searchFriendViewHolder holder,int position, @NonNull User model) {
                int pos = position;
                if(!mUser.getUid().equals(getRef(position).getKey().toString())){
                    Picasso.get().load(model.getImage()).into(holder.circleImageView);
                    holder.tvUserName.setText(model.getHoten());
                }
                else{
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),FriendProfile.class);
                        intent.putExtra("keyValue",getRef(pos).getKey().toString());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public searchFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_friend,parent,false);
                return new searchFriendViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(contactRef,User.class)
                .build();
        FirebaseRecyclerAdapter<User,ContactViewHolder> adapter = new FirebaseRecyclerAdapter<User, ContactViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactViewHolder holder, int position, @NonNull User model) {
                int pos = position;
                String userId = getRef(position).getKey();
                mUserRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = snapshot.child("hoten").getValue().toString();
                        String img = snapshot.child("image").getValue().toString();
                        Picasso.get().load(img).into(holder.circleImageView1);
                        holder.txtUserName1.setText(username);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(),ChatActivity.class);
                    intent.putExtra("yourUid",getRef(pos).getKey().toString());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_friend,parent,false);
                return new ContactViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class ContactViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageView1;
        TextView txtUserName1;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView1 = itemView.findViewById(R.id.circleImageView1);
            txtUserName1 = itemView.findViewById(R.id.txtUserName1);
        }
    }
}