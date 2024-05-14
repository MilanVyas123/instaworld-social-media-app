package com.example.sessionexample.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sessionexample.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private ArrayList<String> data;
    private Context context;
    private ImagePickListener listener;

    private FirebaseDatabase database;

    private String userNameKey;

    private ArrayList<String> storyUsers;

    public StoryAdapter(Context context, ArrayList<String> data, ImagePickListener listener) {
        this.data = data;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.story_item, parent, false);
        storyUsers=new ArrayList<>();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        database = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = database.getReference("Users");
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();




        if (position == 0) {



            userNameKey=user.getUid();


            checkSelfStory(new CheckStoryListener() {
                @Override
                public void onStoryChecked(boolean storyExists) {
                    if (storyExists) {
                        // Do something if story exists
                        // For example, enable some functionality
                        // Toast.makeText(context, "true", Toast.LENGTH_SHORT).show();
                        holder.plusIcon.setVisibility(View.GONE);
                        holder.itemView.setOnClickListener(view -> {
                            if (listener != null) {
                                listener.viewStory(userNameKey);
                            }
                        });

                    } else {
                        // Do something if story doesn't exist
                        // For example, hide some views
                        // Toast.makeText(context, "false", Toast.LENGTH_SHORT).show();
                        holder.plusIcon.setVisibility(View.VISIBLE);
                        holder.itemView.setOnClickListener(view -> {
                            if (listener != null) {
                                listener.onImagePicked();
                            }
                        });
                    }
                }

                @Override
                public void onStoryCheckFailed(DatabaseError databaseError) {
                    // Handle the database error
                }
            });


            //Toast.makeText(context, userNameKey, Toast.LENGTH_SHORT).show();
            DatabaseReference specificUserReference = usersReference.child(userNameKey);
            specificUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                    String imageUrl= userSnapshot.child("profileImage").getValue(String.class);
                    Log.d("profileImage",imageUrl);
                    Picasso.get().load(imageUrl).into(holder.circleImageView);
                    holder.textView.setText("Your story");


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("storyProfileError",databaseError.toString());
                }
            });


        } else {
            holder.plusIcon.setVisibility(View.GONE);
            String userKey=data.get(position);

            //holder.textView.setText(data.get(position));
            DatabaseReference databaseReference=database.getReference("Users");
            databaseReference.child(userKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userName=dataSnapshot.child("name").getValue(String.class);
                    String imageUrl= dataSnapshot.child("profileImage").getValue(String.class);
                    holder.textView.setText(userName);
                    Picasso.get().load(imageUrl).into(holder.circleImageView);
                    holder.itemView.setOnClickListener(view -> {
                        listener.viewStory(userKey);
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView textView;
        ImageView plusIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.storyusername);
            plusIcon = itemView.findViewById(R.id.plusIcon);
            circleImageView=itemView.findViewById(R.id.storyProfileImage);
        }
    }
    public void checkSelfStory(CheckStoryListener listener) {
        DatabaseReference storyReference = database.getReference("story");
        DatabaseReference specificUserReference = storyReference.child(userNameKey);
        specificUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    listener.onStoryChecked(true);
                } else {
                    listener.onStoryChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                listener.onStoryCheckFailed(databaseError);
            }
        });
    }

    public interface CheckStoryListener {
        void onStoryChecked(boolean storyExists);
        void onStoryCheckFailed(DatabaseError databaseError);
    }

    public interface ImagePickListener {
        void onImagePicked();
        void viewStory(String userKey);
    }
}
