package com.example.newinstagram.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newinstagram.MainActivity;
import com.example.newinstagram.Model.Comment;
import com.example.newinstagram.Model.User;
import com.example.newinstagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private Context context;
    private List<Comment> mComment;
    String postId;


    private FirebaseUser fUsers;

    public CommentAdapter(Context context, List<Comment> mComment, String postId) {
        this.context = context;
        this.mComment = mComment;
        this.postId = postId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        fUsers= FirebaseAuth.getInstance().getCurrentUser();



        Comment comment=mComment.get(position);
        holder.comment.setText(comment.getComment());

        Log.d("COMMENT ADAPTER", "message is : "+comment.getPublisher());


        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        holder.user_name.setText(user.getUsername());
                        if (user.getImageurl().equals("default")){
                            holder.image_view.setImageResource(R.mipmap.ic_launcher);
                        }else {
                            Picasso.get().load(user.getImageurl()).into(holder.image_view);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, MainActivity.class);
                intent.putExtra("publisherId",comment.getPublisher());
                context.startActivity(intent);
            }
        });
        holder.image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, MainActivity.class);
                intent.putExtra("publisherId",comment.getPublisher());
                context.startActivity(intent);
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (comment.getPublisher().endsWith(fUsers.getUid())){
                    AlertDialog alertDialog=new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Do you want to delete?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseDatabase.getInstance().getReference().child("Comments").child(postId)
                                    .child(comment.getCommentId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(context, "Comment deleted successfully", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                    alertDialog.show();
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView image_view;
        public TextView user_name;
        public TextView comment;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_view=itemView.findViewById(R.id.comment_image);
            user_name=itemView.findViewById(R.id.username_comment);
            comment=itemView.findViewById(R.id.comment2);
        }
    }
}
