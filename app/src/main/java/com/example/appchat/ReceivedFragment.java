package com.example.appchat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.appchat.Adapter.AdapterRequestFriend;
import com.example.appchat.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReceivedFragment extends Fragment {
    private List<User> listUser;
    private FirebaseAuth auth;
    private DatabaseReference userRef,requestRef,contactRef;
    private String currentUserId;
    private AdapterRequestFriend adapterRequestFriend;
    private RecyclerView recyclerViewRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getRequestFriend();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_received, container, false);
        recyclerViewRequest = view.findViewById(R.id.recyclerViewRequest);
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        getRequestReceived();
        return view;
    }
    private void getRequestReceived(){
        listUser = new ArrayList<>();
        requestRef.child(currentUserId).orderByChild("request_type").equalTo("received").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String id = ds.getKey();
                    userRef.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            listUser.add(user);
                            adapterRequestFriend = new AdapterRequestFriend(listUser,getContext());
                            recyclerViewRequest.setAdapter(adapterRequestFriend);
//                            adapterRequestFriend.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Loi", Toast.LENGTH_SHORT).show();
                        }
                    });
//                    adapterRequestFriend.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Loi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}