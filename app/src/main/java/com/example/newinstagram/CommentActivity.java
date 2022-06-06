package com.example.newinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newinstagram.Adapter.CommentAdapter;
import com.example.newinstagram.Model.Comment;
import com.example.newinstagram.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private RecyclerView recyclerview;
    private List<Comment> comments;
    private CommentAdapter commentAdapter;

    private EditText addtext;
    private CircleImageView imageview;
    private TextView posttext;

    String postId;
    String authorid;

    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar=findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Log.d("COMMENT ACTIVITY", "in on create");

        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");
        authorid=intent.getStringExtra("authorId");

        recyclerview=findViewById(R.id.recycler_view);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        Comment dummyComment = new Comment("123", "random comment","V9OtGXE61pdwz2II7f4rN7vUqRv2V9OtGXE61pdwz2II7f4rN7vUqRv2");

        comments=new ArrayList<Comment>();
        comments.add(dummyComment);
        commentAdapter=new CommentAdapter(this,comments,postId);
        recyclerview.setAdapter(commentAdapter);

        addtext=findViewById(R.id.add_comment);
        imageview=findViewById(R.id.imageprofile);
        posttext=findViewById(R.id.post_txt);

        fUser= FirebaseAuth.getInstance().getCurrentUser();

        getuserImage();
        getComments();

        posttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(addtext.getText().toString())){
                    Toast.makeText(CommentActivity.this, "No comment added", Toast.LENGTH_SHORT).show();
                }else{
                    putComment();
                }
            }
        });
    }

    private void getComments() {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        comments.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            Comment comment=snapshot1.getValue(Comment.class);
                            comments.add(comment);
                        }
                        commentAdapter.notifyDataSetChanged();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void putComment() {

        HashMap<String,Object> map=new HashMap<>();

        Log.d("COMMENT ACTIVITY", "trying to put comment");

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);

        Log.d("COMMENT ACTIVITY", "ref: "+ref.toString());

        String id=ref.push().getKey();
        Log.d("COMMENT ACTIVITY", "id: "+id);
        map.put("commentId",id);
        map.put("Comment",addtext.getText().toString());
        map.put("Publisher",fUser.getUid());

        addtext.setText("");


        ref.child(id).push().setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(CommentActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getuserImage() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        if (user.getImageurl().equals("default")){
                            imageview.setImageResource(R.mipmap.ic_launcher);
                        }else {
                            Picasso.get().load(user.getImageurl()).into(imageview);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}