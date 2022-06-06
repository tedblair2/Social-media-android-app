package com.example.newinstagram.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newinstagram.Adapter.PhotoAdapter;
import com.example.newinstagram.Adapter.PostAdapter;
import com.example.newinstagram.EditActivity;
import com.example.newinstagram.FollowersActivity;
import com.example.newinstagram.Model.Post;
import com.example.newinstagram.Model.User;
import com.example.newinstagram.OptionsActivity;
import com.example.newinstagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class ProfileFragment extends Fragment {
    private RecyclerView recyclerview_profile;
    private PhotoAdapter photoAdapter;
    private List<Post> photolist;

    private RecyclerView recyclerView_saves;
    private PhotoAdapter postAdapter;
    private List<Post> saved_posts;

    private CircleImageView imageprofile;
    private TextView username;
    private TextView fullname;
    private TextView bio;
    private TextView posts;
    private TextView followers;
    private TextView following;
    private ImageView options;
    private Button editprofile;

    private ImageButton mypics;
    private ImageButton savedpics;


    FirebaseUser fUsers;
    String profileId;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        fUsers=FirebaseAuth.getInstance().getCurrentUser();

        String data=getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId","none");

        if (data.equals("none")){
            profileId=fUsers.getUid();
        }else{
            profileId=data;
        }

        imageprofile=view.findViewById(R.id.profile_image);
        username=view.findViewById(R.id.username_profile);
        fullname=view.findViewById(R.id.fullname_profile);
        bio=view.findViewById(R.id.bio_profile);
        posts=view.findViewById(R.id.posts_profile);
        followers=view.findViewById(R.id.followers);
        following=view.findViewById(R.id.following);
        options=view.findViewById(R.id.options);
        mypics=view.findViewById(R.id.mypics);
        savedpics=view.findViewById(R.id.saved_pics);
        editprofile=view.findViewById(R.id.edit_profile);

        recyclerview_profile=view.findViewById(R.id.recycler_profile);
        recyclerview_profile.setHasFixedSize(true);
        recyclerview_profile.setLayoutManager(new GridLayoutManager(getContext(),3));

        photolist=new ArrayList<>();
        photoAdapter=new PhotoAdapter(getContext(),photolist);
        recyclerview_profile.setAdapter(photoAdapter);

        recyclerView_saves=view.findViewById(R.id.recycler_saved);
        recyclerView_saves.setHasFixedSize(true);
        recyclerView_saves.setLayoutManager(new GridLayoutManager(getContext(),3));

        saved_posts=new ArrayList<>();
        postAdapter=new PhotoAdapter(getContext(),saved_posts);
        recyclerView_saves.setAdapter(photoAdapter);

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), OptionsActivity.class));
            }
        });




        if(profileId.equals(fUsers.getUid())){
            editprofile.setText("Edit Profile");
        }else {
            checkFollowStatus();
        }

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn_txt=editprofile.getText().toString();
                if (btn_txt.equals("Edit Profile")){
                    startActivity(new Intent(getContext(), EditActivity.class));
                }else{
                    if (btn_txt.equals("follow")){
                        FirebaseDatabase.getInstance().getReference().child("follow")
                                .child(fUsers.getUid()).child("following").child(profileId).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("follow").child(profileId)
                                .child("followers").child(fUsers.getUid()).setValue(true);
                    }else {
                        FirebaseDatabase.getInstance().getReference().child("follow")
                                .child(fUsers.getUid()).child("following").child(profileId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("follow").child(profileId)
                                .child("followers").child(fUsers.getUid()).removeValue();
                    }
                }
            }
        });
        recyclerview_profile.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);

        mypics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerview_profile.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);

            }
        });
        savedpics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerview_profile.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
            }
        });

        userinfo();
        followersandfollowersCount();
        getPostCount();
        myPhotos();
        getSavedPosts();

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","followers");
                startActivity(intent);
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","following");
                startActivity(intent);
            }
        });

        return view;
    }
    private void getSavedPosts() {
        List<String> saved_ids=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(fUsers.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            saved_ids.add(snapshot1.getKey());
                        }
                        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener
                                (new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                        saved_posts.clear();
                                        for (DataSnapshot snapshot2:snapshot3.getChildren()){
                                            Post post=snapshot2.getValue(Post.class);

                                            for (String id:saved_ids){
                                                if (post.getPostid().equals(id)){
                                                    saved_posts.add(post);
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void myPhotos() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        photolist.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            Post post=snapshot1.getValue(Post.class);
                            if (post.getPublisher().equals(profileId)){
                                photolist.add(post);
                            }
                        }
                        Collections.reverse(photolist);
                        photoAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void checkFollowStatus() {
        FirebaseDatabase.getInstance().getReference().child("follow").child(fUsers.getUid()).child("following")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(profileId).exists()){
                            editprofile.setText("following");
                        }else {
                            editprofile.setText("follow");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getPostCount() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int counter=0;
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            Post post=snapshot1.getValue(Post.class);
                            if (post.getPublisher().equals(profileId))
                                counter++;
                        }
                        posts.setText(String.valueOf(counter));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void followersandfollowersCount() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("follow").child(profileId);
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userinfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);

                        if (user.getImageurl().equals("default")){
                            imageprofile.setImageResource(R.drawable.ic_person);
                        }else{
                            Picasso.get().load(user.getImageurl()).into(imageprofile);
                        }
                        username.setText(user.getUsername());
                        fullname.setText(user.getName());
                        bio.setText(user.getBio());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}