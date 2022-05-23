package com.example.newinstagram.Fragments;

import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.newinstagram.Adapter.TagAdapter;
import com.example.newinstagram.Adapter.UserAdapter;
import com.example.newinstagram.Model.User;
import com.example.newinstagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private SocialAutoCompleteTextView search;
    private List<User> users;
    private UserAdapter userAdapter;

    private RecyclerView recyclerViewtags;
    private List<String> mTags;
    private List<String> mTagscount;
    private TagAdapter tagAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView=view.findViewById(R.id.users);
        search=view.findViewById(R.id.search_bar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewtags=view.findViewById(R.id.tags);
        recyclerViewtags.setHasFixedSize(true);
        recyclerViewtags.setLayoutManager(new LinearLayoutManager(getContext()));

        users=new ArrayList<>();
        mTags=new ArrayList<>();
        mTagscount=new ArrayList<>();

        userAdapter=new UserAdapter(getContext(),users,true);
        recyclerView.setAdapter(userAdapter);
        tagAdapter=new TagAdapter(getContext(),mTags,mTagscount);
        recyclerViewtags.setAdapter(tagAdapter);

        readusers();
        readtags();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Searchuser(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());

            }
        });


        return view;
    }

    private void readtags() {
        FirebaseDatabase.getInstance().getReference().child("Hashtags").addValueEventListener
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mTags.clear();
                        mTagscount.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            mTags.add(snapshot1.getKey());
                            mTagscount.add(snapshot1.getChildrenCount()+"");
                        }
                        tagAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void readusers() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(search.getText().toString())){
                    users.clear();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        User user=snapshot1.getValue(User.class);
                        users.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void Searchuser(String s){
        Query query=FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username")
                .startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    User user=snapshot1.getValue(User.class);
                    users.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void filter(String text){
        List<String> tagsSearch=new ArrayList<>();
        List<String> tagsSearchcount=new ArrayList<>();

        for (String s:mTags){
            if (s.toLowerCase().contains(text.toLowerCase())){
                tagsSearch.add(s);
                tagsSearchcount.add(mTagscount.get(mTags.indexOf(s)));
            }
        }
        tagAdapter.filter(tagsSearch,tagsSearchcount);
    }

}