package com.example.sessionexample;

import android.content.Context;
import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchData extends RecyclerView.Adapter<SearchData.MyViewHolder> {

    private ArrayList<Model> mList;
    private Context context;

    private static OnItemClickListener listener;

    public SearchData(Context context , ArrayList<Model> mList)
    {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Model model = mList.get(position);
        String imageUrl = model.getImageUrl();
        //Glide.with(context).load(mList.get(position).getImageUrl()).into(holder.circleImageView);

        Picasso.get().load(mList.get(position).getImageUrl()).into(holder.circleImageView);
        holder.username.setText(model.getUsername());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void clear() {
        if (mList != null) {
            mList.clear();
            notifyDataSetChanged(); // Notify the adapter that the data has changed
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder
    {

        CircleImageView circleImageView;
        TextView username;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView  = itemView.findViewById(R.id.userProfileImage);

            username = itemView.findViewById(R.id.username);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }


    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public String getNameAtPosition(int position) {
        if (position >= 0 && position < mList.size()) {
            return mList.get(position).getUsername(); // Replace this with how you access the name in your item model
        } else {
            return null;
        }
    }

}
