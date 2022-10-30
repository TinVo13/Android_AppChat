package com.example.appchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appchat.Adapter.AdapterGroupChatList;
import com.example.appchat.Entity.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    private FloatingActionButton btnCreate;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private ArrayList<Group> groupArrayList;
    private AdapterGroupChatList adapterGroupChatList;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewGroup);
        auth = FirebaseAuth.getInstance();
        
        loadGroupChatList();
        return view;
    }

    private void loadGroupChatList() {
        groupArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //neu nguoi dung hien tai co trong participants cua nhom chat thi hien thi danh sach cac nhom chat co nguoi dung tham gia
                    if(ds.child("Participants").child(auth.getUid()).exists()){
                        Group group = ds.getValue(Group.class);
                        groupArrayList.add(group);
                        //Toast.makeText(getContext(), ""+group, Toast.LENGTH_SHORT).show();
                    }
                }
                adapterGroupChatList = new AdapterGroupChatList(getActivity(),groupArrayList);
                recyclerView.setAdapter(adapterGroupChatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void searchGroupChat(String query) {
        groupArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //neu nguoi dung hien tai co trong participants cua nhom chat thi hien thi danh sach cac nhom chat co nguoi dung tham gia
                    if(ds.child("Participants").child(auth.getUid()).exists()){
                        //tim kiem nhom chat theo ten nhom
                        if(ds.child("groupTitle").toString().toLowerCase().contains(query.toLowerCase())){
                            Group group = ds.getValue(Group.class);
                            groupArrayList.add(group);
                        }

                    }
                }
                adapterGroupChatList = new AdapterGroupChatList(getContext(),groupArrayList);
                recyclerView.setAdapter(adapterGroupChatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
}