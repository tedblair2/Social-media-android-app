package com.example.newinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.example.newinstagram.Adapter.UserAdapter;
import com.example.newinstagram.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {
    private String id;
    private String title;
    private RecyclerView recyclerView;
    private List<String> idList;
    private UserAdapter userAdapter;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        title=intent.getStringExtra("title");

        Toolbar toolbar=findViewById(R.id.toolbar_follow);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView=findViewById(R.id.recycler_follow);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        users=new ArrayList<>();
        userAdapter=new UserAdapter(this,users,false);
        recyclerView.setAdapter(userAdapter);

        idList=new ArrayList<>();

        switch (title){
            case "followers":
                getFollowers();
                break;
            case "following":
                getFollowing();
                break;
            case "likes":
                getLikes();
                break;

        }
    }
    private void getFollowers() {
        FirebaseDatabase.getInstance().getReference().child("follow").child(id).child("followers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        idList.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            idList.add(snapshot1.getKey());
                        }
                        showUsers();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getFollowing() {
        FirebaseDatabase.getInstance().getReference().child("follow").child(id).child("following")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        idList.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            idList.add(snapshot1.getKey());
                        }
                        showUsers();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getLikes() {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        idList.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            idList.add(snapshot1.getKey());
                        }
                        showUsers();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void showUsers() {
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    User user=snapshot1.getValue(User.class);
                    for (String id:idList){
                        if (user.getId().equals(id)){
                            users.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}