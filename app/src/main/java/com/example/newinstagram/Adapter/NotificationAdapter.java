package com.example.newinstagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newinstagram.Fragments.PostDetailFragment;
import com.example.newinstagram.Fragments.ProfileFragment;
import com.example.newinstagram.Model.Notification;
import com.example.newinstagram.Model.Post;
import com.example.newinstagram.Model.User;
import com.example.newinstagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context context;
    private List<Notification> notify;

    public NotificationAdapter(Context context, List<Notification> notify) {
        this.context = context;
        this.notify = notify;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.notification_item,parent,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Notification notification=notify.get(position);

        getUser(holder.imageprofile,holder.usernameNotify,notification.getUserId());
        holder.comment.setText(notification.getText());

        if (notification.isPost()){
            holder.postimage.setVisibility(View.VISIBLE);
            getPostImage(holder.postimage,notification.getPostid());
        }else {
            holder.postimage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.isPost()){
                    context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                            .putString("postid",notification.getPostid()).apply();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,new PostDetailFragment()).commit();

                }else {
                    context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit()
                            .putString("profileId",notification.getUserId()).apply();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,new ProfileFragment()).commit();

                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return notify.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageprofile;
        public ImageView postimage;
        public TextView usernameNotify;
        public TextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageprofile=itemView.findViewById(R.id.imageprofile_notify);
            postimage=itemView.findViewById(R.id.postimage_notify);
            usernameNotify=itemView.findViewById(R.id.username_notify);
            comment=itemView.findViewById(R.id.comment_notify);
        }
    }
    private void getPostImage(ImageView imageView, String postId) {
        FirebaseDatabase.getInstance().getReference("Posts").child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Post post=snapshot.getValue(Post.class);
                        Picasso.get().load(post.getImageurl()).placeholder(R.mipmap.ic_launcher).into(imageView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void getUser(ImageView imageView, TextView txt, String userId) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        if (user.getImageurl().equals("default")){
                            imageView.setImageResource(R.mipmap.ic_launcher);
                        }
                        else {
                            Picasso.get().load(user.getImageurl()).into(imageView);
                        }
                        txt.setText(user.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}
