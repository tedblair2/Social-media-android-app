package com.example.newinstagram.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newinstagram.Adapter.PostAdapter;
import com.example.newinstagram.Model.Post;
import com.example.newinstagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerViewposts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private List<String> followingList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewposts=view.findViewById(R.id.posts);
        recyclerViewposts.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerViewposts.setLayoutManager(layoutManager);

        postList=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),postList);
        recyclerViewposts.setAdapter(postAdapter);

        followingList=new ArrayList<>();

        checkFollowingList();



        return view;
    }

    private void checkFollowingList() {
        FirebaseDatabase.getInstance().getReference().child("follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followingList.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            followingList.add(snapshot1.getKey());
                        }
                        readposts();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void readposts() {
        FirebaseDatabase.getInstance().getReference().child("Posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postList.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            Post post=snapshot1.getValue(Post.class);

                            for (String id:followingList){
                                if (post.getPublisher().equals(id)){
                                    postList.add(post);
                                }
                            }
                        }
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}