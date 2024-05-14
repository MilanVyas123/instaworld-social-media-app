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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewUserActivityData extends RecyclerView.Adapter<ViewUserActivityData.MyViewHolder> {
    private ArrayList<Model> mList;
    private Context context;
    private static OnItemClickListener listener;
    View view;
    String activity,currentUserViewID,specificUserId,currentUserID;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    public ViewUserActivityData(Context context, ArrayList<Model> mList, String activity,String currentUserViewID) {
        this.context = context;
        this.mList = mList;
        this.activity = activity;
        this.currentUserViewID = currentUserViewID;

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();
    }



    @NonNull
    @Override
    public ViewUserActivityData.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.view_user_activity_data,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewUserActivityData.MyViewHolder holder, int position) {

        Model model = mList.get(position);
        String imageUrl = model.getImageUrl();

        Picasso.get().load(mList.get(position).getImageUrl()).into(holder.circleImageView);
        holder.username.setText(model.getUsername());

        if(activity.equals("viewUserFollowing"))
        {
            holder.followButton.setText("Following");
            holder.followButton.setBackgroundColor(Color.parseColor("#F1F8E9"));
            holder.followButton.setTextColor(Color.BLACK);
            holder.followButton.setOnClickListener(null);
            holder.followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = getNameAtPosition(holder.getAdapterPosition());
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

                            mList.clear();

                            DatabaseReference UserIDRef = databaseReference.child("Users");
                            UserIDRef.orderByChild("name").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot getUserID : dataSnapshot.getChildren()) {
                                        String temp = getUserID.getKey();

                                        DatabaseReference removeFollowing = databaseReference.child("following").child(currentUserViewID).child(temp);
                                        removeFollowing.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mList.clear();

                                                DatabaseReference removeFollowers = databaseReference.child("followers").child(temp).child(currentUserViewID);
                                                removeFollowers.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            mList.clear();

                                                            DatabaseReference NewData = databaseReference.child("following").child(currentUserViewID);
                                                            NewData.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                    for (DataSnapshot getNewData : dataSnapshot.getChildren()) {
                                                                        String followerUserId = getNewData.getKey();
                                                                        DatabaseReference userRef = databaseReference.child("Users").child(followerUserId);
                                                                        userRef.addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                if (dataSnapshot.exists()) {
                                                                                    String username = dataSnapshot.child("name").getValue(String.class);
                                                                                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                                                                                    Model model = new Model(imageUrl, username);
                                                                                    mList.add(model);

                                                                                    notifyDataSetChanged();
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                    }
                                                });

                                            }
                                        });
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

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



        }
        else if(activity.equals("viewUserFollowers"))
        {
            holder.followButton.setText("remove");
            holder.followButton.setBackgroundColor(Color.parseColor("#F1F8E9"));
            holder.followButton.setTextColor(Color.BLACK);
            holder.followButton.setOnClickListener(null);
            holder.followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = getNameAtPosition(holder.getAdapterPosition());
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you Sure?");
                    TextView message = new TextView(context);
                    message.setText("\n" + "remove " + username + " from followers");
                    message.setTextColor(Color.RED);
                    message.setTextSize(16);
                    message.setTypeface(null, Typeface.BOLD);
                    message.setGravity(Gravity.CENTER);
                    builder.setView(message);

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mList.clear();

                            DatabaseReference UserIDRef = databaseReference.child("Users");
                            UserIDRef.orderByChild("name").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot getUserID : dataSnapshot.getChildren()) {
                                        String temp = getUserID.getKey();

                                        DatabaseReference removeFollowing = databaseReference.child("followers").child(currentUserViewID).child(temp);
                                        removeFollowing.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mList.clear();

                                                DatabaseReference removeFollowers = databaseReference.child("following").child(temp).child(currentUserViewID);
                                                removeFollowers.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            mList.clear();

                                                            DatabaseReference NewData = databaseReference.child("followers").child(currentUserViewID);
                                                            NewData.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                    for (DataSnapshot getNewData : dataSnapshot.getChildren()) {
                                                                        String followerUserId = getNewData.getKey();
                                                                        DatabaseReference userRef = databaseReference.child("Users").child(followerUserId);
                                                                        userRef.addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                if (dataSnapshot.exists()) {
                                                                                    String username = dataSnapshot.child("name").getValue(String.class);
                                                                                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                                                                                    Model model = new Model(imageUrl, username);
                                                                                    mList.add(model);

                                                                                    notifyDataSetChanged();
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                    }
                                                });

                                            }
                                        });
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
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
