package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.Entity.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestActivity extends AppCompatActivity {
    private ImageButton btnBackRequest;
    private RecyclerView recyclerViewRequest;
    private DatabaseReference requestRef,userRef,contactRef;
    private FirebaseAuth auth;
    private String currentUserId,yourId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        btnBackRequest = findViewById(R.id.btnBackRequest);

        recyclerViewRequest = findViewById(R.id.recyclerViewRequest);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        btnBackRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> optione = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(requestRef.child(currentUserId),User.class)
                .build();

        FirebaseRecyclerAdapter<User,RequestViewHolder> adapter = new FirebaseRecyclerAdapter<User, RequestViewHolder>(optione) {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull User model) {
                int pos = position;
                final String list_user_request = getRef(position).getKey();
                userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = snapshot.child("hoten").getValue().toString();
                        String img = snapshot.child("image").getValue().toString();
                        holder.hoten.setText(username);
                        Picasso.get().load(img).into(holder.image);
                        yourId = getRef(pos).getKey();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RequestActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AcceptFriend(yourId);
                    }
                });
                holder.btnDenie.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                //Toast.makeText(RequestActivity.this, ""+list_user_request, Toast.LENGTH_SHORT).show();
            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                 View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_request_friend,parent,false);
                 RequestViewHolder holder = new RequestViewHolder(view);
                 return holder;
            }
        };
        recyclerViewRequest.setAdapter(adapter);
        adapter.startListening();
    }
    public static class RequestViewHolder extends RecyclerView.ViewHolder{

        TextView hoten;
        CircleImageView image;
        Button btnAccept,btnDenie;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            hoten = itemView.findViewById(R.id.nameTvRequest);
            image = itemView.findViewById(R.id.circleImageViewRequest);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDenie = itemView.findViewById(R.id.btnDenie);
            btnDenie.setVisibility(View.VISIBLE);
            btnAccept.setVisibility(View.VISIBLE);
        }
    }
    private void AcceptFriend(String yourId) {
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
                                                                                    Toast.makeText(RequestActivity.this, "Thêm bạn thành công", Toast.LENGTH_SHORT).show();
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