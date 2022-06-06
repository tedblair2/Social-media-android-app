package com.example.newinstagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.newinstagram.Fragments.ProfileFragment;
import com.example.newinstagram.MainActivity;
import com.example.newinstagram.Model.User;
import com.example.newinstagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private Context context;
    private List<User> mUsers;
    private boolean isFragment;

    private FirebaseUser firebaseUser;


    public UserAdapter(Context context, List<User> mUsers, boolean isFragment) {
        this.context = context;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        User user=mUsers.get(position);
        holder.follow.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getName());

        Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageprofile);
        isFollwed(user.getId(),holder.follow);

        if (user.getId().equals(firebaseUser.getUid())){
            holder.follow.setVisibility(View.GONE);
        }
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.follow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()
                    ).child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                    addNotification(user.getId());
                }else{
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()
                    ).child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).removeValue();

                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFragment){
                    context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit()
                            .putString("profileId", user.getId()).apply();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,new ProfileFragment()).commit();
                }else {
                    Intent intent=new Intent(context, MainActivity.class);
                    intent.putExtra("publisherId",user.getId());
                    context.startActivity(intent);
                }
            }
        });
    }

    private void addNotification(String id) {
        HashMap<String,Object> map=new HashMap<>();
        map.put("userId",id);
        map.put("text","started following you");
        map.put("postId","");
        map.put("isPost",false);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid())
                .push().setValue(map);
    }

    private void isFollwed(String id, Button follow) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("follow")
                .child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(id).exists())
                    follow.setText("following");
                else follow.setText("follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView imageprofile;
        public TextView username;
        public TextView fullname;
        public Button follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageprofile=itemView.findViewById(R.id.image_profile);
            username=itemView.findViewById(R.id.user_name);
            fullname=itemView.findViewById(R.id.full_name);
            follow=itemView.findViewById(R.id.btn_follow);


        }
    }
}
