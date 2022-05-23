package com.example.newinstagram.Adapter;

import android.content.Context;
import android.media.Image;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.newinstagram.Model.Post;
import com.example.newinstagram.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>{
    private Context context;
    private List<Post> post;

    public PhotoAdapter(Context context, List<Post> post) {
        this.context = context;
        this.post = post;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.photo_item,parent,false);
        return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post mpost= post.get(position);
        Picasso.get().load(mpost.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.postimage);

    }

    @Override
    public int getItemCount() {
        return post.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView postimage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postimage=itemView.findViewById(R.id.post_image);
        }
    }
}
