package com.example.sessionexample.adapter;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sessionexample.PostFeed;
import com.example.sessionexample.R;
import com.example.sessionexample.comment.CommentActivity;
import com.example.sessionexample.databinding.ActivityMainBinding;
import com.example.sessionexample.viewUserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<PostFeed> postFeedList;
    FirebaseAuth mAuth;
    String currentUserName;
    private ActivityMainBinding binding;
    private boolean isLiked = false;
    DatabaseReference databaseReference;
    FirebaseDatabase database;

    public String currentuserkey;
    public void getCurrentUsername(){

        DatabaseReference CurrentUsernameRef = databaseReference.child("Users").child(mAuth.getUid());
        CurrentUsernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUserName = dataSnapshot.child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public RecyclerViewAdapter(Context context, ArrayList<PostFeed> postFeedList) {
        currentuserkey=FirebaseAuth.getInstance().getUid();
        this.context = context;
        this.postFeedList = postFeedList;
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();

        getCurrentUsername();

    }
    public void readData(String path, FirebaseCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Data successfully read, trigger success callback
                callback.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // An error occurred, trigger failure callback
                callback.onFailure(databaseError);
            }
        });
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.post_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
Log.d("size", String.valueOf(getItemCount()));

        PostFeed postFeed=postFeedList.get(position);
        readData("likes/"+postFeed.getPostUserId()+"/"+postFeed.getPostId(), new FirebaseCallback() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                // Handle successful data retrieval here
                postFeed.setLikesCount((int) dataSnapshot.getChildrenCount());

                holder.likesCount.setText(postFeed.getLikesCount());
                if(dataSnapshot.hasChild(currentuserkey))
                {
                    holder.likeIcon.setImageResource(R.drawable.baseline_favorite_24);
                    postFeed.setLiked(true);
                }
                else{
                    postFeed.setLiked(false);
                }
                Log.d("isliked", String.valueOf(postFeed.getLiked())+postFeed.getPostId());
            }

            @Override
            public void onFailure(DatabaseError databaseError) {
                // Handle failure here
            }
        });

        holder.userName.setText(postFeed.getUserName());
        holder.caption.setText(postFeed.getCaption());
        String postURL=postFeed.getImageURL();
        String profileImageURL=postFeed.getProfileImage();
        Picasso.get().load(profileImageURL).into(holder.profileImage);
        Picasso.get().load(postURL).into(holder.postImage);


        Animation zoomInAnim = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
        Animation zoomOutAnim = AnimationUtils.loadAnimation(context, R.anim.zoom_out);

        holder.postImage.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
               // holder.insideImage.setVisibility(View.VISIBLE);
                holder.likeIcon.setImageResource(R.drawable.baseline_favorite_24);
                holder.likeIcon.startAnimation(zoomInAnim);
                holder.insideImage.startAnimation(zoomInAnim);
                holder.insideImage.startAnimation(zoomOutAnim);

                Log.d("liked",postFeed.getPostUserId()+" "+postFeed.getPostId());
                String postUserId=postFeed.getPostUserId();
                String postId=postFeed.getPostId();
                DatabaseReference likeRefernce= FirebaseDatabase.getInstance().getReference("likes");
                likeRefernce.child(postUserId).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChild(currentuserkey))
                        {
                            Map<String, Object> postDetails = new HashMap<>();
                            postDetails.put(currentuserkey,"liked");
                            holder.likesCount.setText(postFeed.increaseLike());


                            if (postId != null) {
                                likeRefernce.child(postUserId).child(postId).updateChildren(postDetails)
                                        .addOnSuccessListener(aVoid -> {
                                            postFeed.setLiked(!postFeed.getLiked());
                                            // Data successfully added to the database
                                            // You can display a success message or take further actions here
                                            //progressBar.setVisibility(View.GONE);
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle any errors that occurred while adding data to the database
                                            //progressBar.setVisibility(View.GONE);
                                        });
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        holder.likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("postid", String.valueOf(postFeed.getLiked()));
                if (postFeed.getLiked()) {
                    holder.likesCount.setText(postFeed.decreaseLike());

                    holder.likeIcon.setImageResource(R.drawable.baseline_favorite_border_24);
                    readData("likes/"+postFeed.getPostUserId()+"/"+postFeed.getPostId(), new FirebaseCallback() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            DatabaseReference childReference = dataSnapshot.getRef().child(currentuserkey);


                            // Remove the data
                            childReference.removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        postFeed.setLiked(!postFeed.getLiked());
                                        // Data successfully removed
                                        // You can perform additional actions here if needed
                                    })
                                    .addOnFailureListener(e -> {
                                        // An error occurred while removing data
                                        // Handle the error appropriately
                                    });

                        }

                        @Override
                        public void onFailure(DatabaseError databaseError) {

                        }
                    });

                } else {
                    holder.likesCount.setText(postFeed.increaseLike());
                    holder.likeIcon.setImageResource(R.drawable.baseline_favorite_24);
                    holder.insideImage.startAnimation(zoomInAnim);
                    holder.insideImage.startAnimation(zoomOutAnim);
                    Log.d("liked",postFeed.getPostUserId()+" "+postFeed.getPostId());
                    String postUserId=postFeed.getPostUserId();
                    String postId=postFeed.getPostId();
                    DatabaseReference likeRefernce= FirebaseDatabase.getInstance().getReference("likes");
                    likeRefernce.child(postUserId).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.hasChild(currentuserkey))
                            {
                                Map<String, Object> postDetails = new HashMap<>();
                                postDetails.put(currentuserkey,"liked");


                                if (postId != null) {
                                    likeRefernce.child(postUserId).child(postId).updateChildren(postDetails)
                                            .addOnSuccessListener(aVoid -> {
                                                postFeed.setLiked(!postFeed.getLiked());
                                                // Data successfully added to the database
                                                // You can display a success message or take further actions here
                                                //progressBar.setVisibility(View.GONE);
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle any errors that occurred while adding data to the database
                                                //progressBar.setVisibility(View.GONE);
                                            });
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                holder.likeIcon.startAnimation(zoomInAnim);


            }
        });

        holder.commentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentPostId=postFeed.getPostId();
                String postUserId=postFeed.getPostUserId();
                // Intent intent = new Intent(getContext(), ViewStory.class);
                // intent.putExtra("userkey", userKey);

                //startActivity(intent);
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId",commentPostId);
                intent.putExtra("postUserId",postUserId);




                // Start the new activity
                context.startActivity(intent);

            }
        });


        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, viewUserProfile.class);
                i.putExtra("username",holder.userName.getText().toString());

                if(currentUserName.equals(holder.userName.getText().toString()))
                {
                    i.putExtra("currentUserName",currentUserName);
                }
                else
                {
                    i.putExtra("currentUserName","");
                }

                context.startActivity(i);
            }
        });


    }
    abstract static class DoubleClickListener implements View.OnClickListener {

        private long lastClickTime = 0;

        private static final int DOUBLE_CLICK_TIME_DELTA = 300;

        @Override
        public void onClick(View v) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v);
            }
            lastClickTime = clickTime;
        }

        abstract void onDoubleClick(View v);
    }

    @Override
    public int getItemCount() {
        return postFeedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView userName;
        public TextView caption;
        public ImageView postImage;

        public ImageView profileImage;

        public ImageView insideImage;

        public ImageView likeIcon;

        public TextView likesCount;
        public ImageView commentIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            likesCount=itemView.findViewById(R.id.likesCount);
            userName = itemView.findViewById(R.id.username);
            postImage = itemView.findViewById(R.id.postImage);
            caption = itemView.findViewById(R.id.caption);
            profileImage=itemView.findViewById(R.id.userPhoto);
            insideImage=itemView.findViewById(R.id.centerImage);
            likeIcon=itemView.findViewById(R.id.likeIcon);
            commentIcon=itemView.findViewById(R.id.commentIcon);
            postImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("ClickFromViewHolder", "Clicked");


        }

    }
    public interface FirebaseCallback {
        void onSuccess(DataSnapshot dataSnapshot);
        void onFailure(DatabaseError databaseError);
    }
}



