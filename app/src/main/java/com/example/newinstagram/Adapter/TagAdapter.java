package com.example.newinstagram.Adapter;

import android.content.Context;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.newinstagram.R;

import org.w3c.dom.Text;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder>{
    private Context context;
    private List<String> tags;
    private List<String> tagscount;

    public TagAdapter(Context context, List<String> tags, List<String> tagscount) {
        this.context = context;
        this.tags = tags;
        this.tagscount = tagscount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.tag_item,parent,false);
        return new TagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tag.setText("#"+tags.get(position));
        holder.posts.setText(tagscount.get(position)+" posts");

    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tag;
        public TextView posts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tag=itemView.findViewById(R.id.hashtag);
            posts=itemView.findViewById(R.id.no_of_posts);

        }
    }
    public void filter(List<String> filtertags,List<String> filtertagscount){
        this.tags=filtertags;
        this.tagscount=filtertagscount;

        notifyDataSetChanged();
    }

}
