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
    private DatabaseReference ref,userRef;
    private FirebaseAuth auth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        btnBackRequest = findViewById(R.id.btnBackRequest);

        recyclerViewRequest = findViewById(R.id.recyclerViewRequest);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        ref = FirebaseDatabase.getInstance().getReference().child("Requests");

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
                .setQuery(ref.child(currentUserId),User.class)
                .build();

        FirebaseRecyclerAdapter<User,RequestViewHolder> adapter = new FirebaseRecyclerAdapter<User, RequestViewHolder>(optione) {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull User model) {
                holder.itemView.findViewById(R.id.btnAccept).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.btnDenie).setVisibility(View.VISIBLE);

                final String list_user_request = getRef(position).getKey();

                //Toast.makeText(RequestActivity.this, ""+list_user_request, Toast.LENGTH_SHORT).show();
                DatabaseReference reference = getRef(position).child("status").getRef();

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String type = snapshot.getValue().toString();
                            if(type.equals("pending")){
                                userRef.orderByChild("uid").equalTo(list_user_request).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds: snapshot.getChildren()){
                                            final String requestUserName = ds.child("hoten").getValue().toString();
                                            final String requestImage = ds.child("image").getValue().toString();
                                            holder.hoten.setText(requestUserName);
                                            Picasso.get().load(requestImage).into(holder.image);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                        Toast.makeText(RequestActivity.this, "Khong tim thay", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        //}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(RequestActivity.this, "khong tim thay", Toast.LENGTH_SHORT).show();
                    }
                });
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
        }
    }
}