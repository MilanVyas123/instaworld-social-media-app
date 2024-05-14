package com.example.sessionexample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewAnotherUserActivityData extends RecyclerView.Adapter<ViewAnotherUserActivityData.MyViewHolder> {
    private static OnFollowButtonClickListener followButtonClickListener;
    private ArrayList<Model> mList;
    private Context context;
    private static ViewAnotherUserActivityData.OnItemClickListener listener;
    View view;
    String activity,currentUserViewID,specificUserId,currentUserID;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    List<String> followingIds = new ArrayList<>();
    List<String> followerIds = new ArrayList<>();
    public ViewAnotherUserActivityData(Context context, ArrayList<Model> mList, String activity,String currentUserViewID, OnFollowButtonClickListener followButtonClickListener) {
        this.context = context;
        this.mList = mList;
        this.activity = activity;
        this.currentUserViewID = currentUserViewID;
        this.followButtonClickListener = followButtonClickListener;

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();
        getMyFollowingId();
        getMyFollowerId();
    }

    private void getMyFollowingId()
    {
        DatabaseReference myFollowing = databaseReference.child("following").child(currentUserID);
        myFollowing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot getFollowingId : dataSnapshot.getChildren())
                {
                    followingIds.add(getFollowingId.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getMyFollowerId()
    {
        DatabaseReference myFollowers = databaseReference.child("followers").child(currentUserID);
        myFollowers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot getFollowerId : dataSnapshot.getChildren())
                {
                    followerIds.add(getFollowerId.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public ViewAnotherUserActivityData.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.view_another_user_activity_data,parent,false);
        return new ViewAnotherUserActivityData.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model model = mList.get(position);
        String imageUrl = model.getImageUrl();
        Picasso.get().load(mList.get(position).getImageUrl()).into(holder.circleImageView);
        holder.username.setText(model.getUsername());

        if(activity.equals("viewUserFollowing"))
        {
            String username = getNameAtPosition(holder.getAdapterPosition());

            DatabaseReference usernameId = databaseReference.child("Users");
            usernameId.orderByChild("name").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        String key = dataSnapshot1.getKey();

                        for(String followingId : followingIds)
                        {
                            if(followingId.equals(key))
                            {
                                holder.followButton.setText("Following");
                                holder.followButton.setBackgroundColor(Color.parseColor("#F1F8E9"));
                                holder.followButton.setTextColor(Color.BLACK);
                                holder.followButton.setOnClickListener(null);
                                holder.followButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setTitle("Are you Sure?");
                                        TextView message = new TextView(context);
                                        message.setText("\n" + "Unfollow " + username);
                                        message.setTextColor(Color.RED);
                                        message.setTextSize(20);
                                        message.setTypeface(null, Typeface.BOLD);
                                        message.setGravity(Gravity.CENTER);
                                        builder.setView(message);
                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                DatabaseReference removeFollowing = databaseReference.child("following").child(mAuth.getUid()).child(key);
                                                removeFollowing.removeValue();
                                                DatabaseReference removeFollow = databaseReference.child("followers").child(key).child(mAuth.getUid());
                                                removeFollow.removeValue();

                                                holder.followButton.setVisibility(View.GONE);
                                                notifyDataSetChanged();
                                            }
                                        });
                                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                        builder.show();
                                    }
                                });
                                break;
                            }
                            if(key.equals(mAuth.getUid()))
                            {
                                holder.followButton.setVisibility(View.GONE);
                                holder.followButton.setOnClickListener(null);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else if(activity.equals("viewUserFollowers"))
        {
            String username = getNameAtPosition(holder.getAdapterPosition());

            DatabaseReference usernameId = databaseReference.child("Users");
            usernameId.orderByChild("name").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        String key = dataSnapshot1.getKey();

                        for(String followerId : followingIds)
                        {
                            if(followerId.equals(key))
                            {
                                holder.followButton.setText("Following");
                                holder.followButton.setBackgroundColor(Color.parseColor("#F1F8E9"));
                                holder.followButton.setTextColor(Color.BLACK);
                                holder.followButton.setOnClickListener(null);
                                holder.followButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setTitle("Are you Sure?");
                                        TextView message = new TextView(context);
                                        message.setText("\n" + "Unfollow " + username);
                                        message.setTextColor(Color.RED);
                                        message.setTextSize(20);
                                        message.setTypeface(null, Typeface.BOLD);
                                        message.setGravity(Gravity.CENTER);
                                        builder.setView(message);
                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                DatabaseReference removeFollowing = databaseReference.child("following").child(mAuth.getUid()).child(key);
                                                removeFollowing.removeValue();
                                                DatabaseReference removeFollow = databaseReference.child("followers").child(key).child(mAuth.getUid());
                                                removeFollow.removeValue();

                                                holder.followButton.setVisibility(View.GONE);
                                                notifyDataSetChanged();
                                            }
                                        });
                                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                        builder.show();
                                    }
                                });
                                break;
                            }
                            if(key.equals(mAuth.getUid()))
                            {
                                holder.followButton.setVisibility(View.GONE);
                                holder.followButton.setOnClickListener(null);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

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
    public interface OnFollowButtonClickListener {
        void onFollowButtonClick(int position);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView circleImageView;
        TextView username;
        Button followButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView  = itemView.findViewById(R.id.userProfileImage);

            username = itemView.findViewById(R.id.username);

            followButton = itemView.findViewById(R.id.followButton);

            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (followButtonClickListener != null) {
                            followButtonClickListener.onFollowButtonClick(position);
                        }
                    }
                }
            });


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
    public void setOnItemClickListener(ViewAnotherUserActivityData.OnItemClickListener listener) {
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
