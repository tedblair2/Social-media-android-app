package com.example.newinstagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newinstagram.CommentActivity;
import com.example.newinstagram.FollowersActivity;
import com.example.newinstagram.Fragments.PostDetailFragment;
import com.example.newinstagram.Fragments.ProfileFragment;
import com.example.newinstagram.Model.Post;
import com.example.newinstagram.Model.User;
import com.example.newinstagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{//for loading the images posted by users on the home fragment
    private Context context;
    private List<Post> mPosts;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.post_item,parent,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Post post=mPosts.get(position);
        Picasso.get().load(post.getImageurl()).into(holder.postimage);
        holder.description.setText(post.getDescription());

        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);

                        if (user.getImageurl().equals("default")){
                            holder.profileimage.setImageResource(R.mipmap.ic_launcher);
                        }else {
                            Picasso.get().load(user.getImageurl()).into(holder.profileimage);
                        }
                        holder.username.setText(user.getUsername());
                        holder.author.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        isLiked(post.getPostid(),holder.like);
        noOfLikes(post.getPostid(),holder.noOfLikes);
        noOfComments(post.getPostid(),holder.noOfComments);
        isSaved(post.getPostid(),holder.save);


        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);

                    addNotification(post.getPostid(),post.getPublisher());
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, CommentActivity.class);
                intent.putExtra("postId",post.getPostid());
                intent.putExtra("authorId",post.getPublisher());
                context.startActivity(intent);
            }
        });
        holder.noOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, CommentActivity.class);
                intent.putExtra("postId",post.getPostid());
                intent.putExtra("authorId",post.getPublisher());
                context.startActivity(intent);
            }
        });
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(post.getPostid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(post.getPostid()).removeValue();
                }

            }
        });
        holder.profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit()
                        .putString("profileId", post.getPublisher()).apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new ProfileFragment()).commit();
            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit()
                        .putString("profileId", post.getPublisher()).apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new ProfileFragment()).commit();
            }
        });
        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit()
                        .putString("profileId", post.getPublisher()).apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new ProfileFragment()).commit();
            }
        });
        holder.postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                        .putString("postid", post.getPostid()).apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new PostDetailFragment()).commit();
            }
        });
        holder.noOfLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, FollowersActivity.class);
                intent.putExtra("id",post.getPublisher());
                intent.putExtra("title","likes");
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView profileimage;
        public ImageView postimage;
        public ImageView save;
        public ImageView like;
        public ImageView comment;
        public ImageView more;

        public TextView username;
        public TextView noOfLikes;
        public TextView author;
        public TextView noOfComments;
        SocialTextView description;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileimage=itemView.findViewById(R.id.profilepic);
            postimage=itemView.findViewById(R.id.postimage);
            save=itemView.findViewById(R.id.save);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            more=itemView.findViewById(R.id.more);

            username=itemView.findViewById(R.id.username2);
            noOfLikes=itemView.findViewById(R.id.no_of_likes);
            author=itemView.findViewById(R.id.author);
            noOfComments=itemView.findViewById(R.id.no_of_comments);
            description=itemView.findViewById(R.id.description);
        }
    }
    private void isSaved(String postid, ImageView save) {
        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(postid).exists()){
                            save.setImageResource(R.drawable.ic_saved);
                            save.setTag("saved");
                        }else {
                            save.setImageResource(R.drawable.ic_save);
                            save.setTag("save");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void isLiked(String postid,ImageView imageview){ //like method
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postid).addValueEventListener
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(firebaseUser.getUid()).exists()){
                            imageview.setImageResource(R.drawable.ic_liked);
                            imageview.setTag("liked");
                        }else{
                            imageview.setImageResource(R.drawable.ic_like);
                            imageview.setTag("like");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void noOfLikes(String postid,TextView text){ //no of likes method
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postid).addValueEventListener
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        text.setText(snapshot.getChildrenCount()+" likes");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void noOfComments(String postid,TextView txt){
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postid).addValueEventListener
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        txt.setText("View all "+snapshot.getChildrenCount()+" comments.");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void addNotification(String postid, String publisherid) {
        HashMap<String,Object> map=new HashMap<>();
        map.put("userId",publisherid);
        map.put("text","liked your post");
        map.put("postId",postid);
        map.put("isPost",true);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid())
                .push().setValue(map);
    }

}
