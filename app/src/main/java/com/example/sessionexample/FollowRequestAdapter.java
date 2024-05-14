package com.example.sessionexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowRequestAdapter extends RecyclerView.Adapter<FollowRequestAdapter.MyViewHolder>  {
    private static ArrayList<Model> mList;
    private Context context;
    private static OnItemClickListener listener;
    public FollowRequestAdapter(Context context , ArrayList<Model> mList)
    {
        this.context = context;
        this.mList = mList;
    }


    @NonNull
    @Override
    public FollowRequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.followrequestlist,parent,false);
        return new FollowRequestAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowRequestAdapter.MyViewHolder holder, int position) {

        Model model = mList.get(position);
        String imageUrl = model.getImageUrl();

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

    public void removeItem(int position)
    {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position,String action,String username);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView circleImageView;
        TextView username;
        Button Accept,Decline;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView  = itemView.findViewById(R.id.userProfileImage);

            username = itemView.findViewById(R.id.username);

            Accept = itemView.findViewById(R.id.Accept);

            Decline = itemView.findViewById(R.id.Decline);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position,"ViewProfile",mList.get(position).getUsername());
                        }
                    }
                }
            });

            Accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position,"Accept",mList.get(position).getUsername());
                        }
                    }
                }
            });

            Decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position,"Decline",mList.get(position).getUsername());
                        }
                    }
                }
            });

        }
    }

}
