package com.example.sessionexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sessionexample.reels.ReelAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class viewUserProfile extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    String userID,currentUserID,getUserId,username,userProfileImageURL,currentUserName;
    long followerCount=0,followingCount=0,postCount=0;
    TextView followers,following,userName,posts,userBIO,followerInfo,followingInfo;
    CircleImageView userProfile;
    Button followUser;
    private boolean isFollowersUpdated = false;
    GridView gridView,reelGridLayout;
    ImageGridAdapter gridAdapter;
    ReelAdapter reelAdapter;
    View horizontalLineForPostLine,horizontalLineForReelLine;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);

        followers = findViewById(R.id.followerCount);
        followerInfo = findViewById(R.id.followerInfo);
        following = findViewById(R.id.followingCount);
        followingInfo = findViewById(R.id.followingInfo);
        posts = findViewById(R.id.postCount);
        userProfile = findViewById(R.id.profileImage);
        userName = findViewById(R.id.nameTextView);
        followUser = findViewById(R.id.followUser);
        userBIO = findViewById(R.id.userBIO);
        horizontalLineForPostLine = findViewById(R.id.horizontalLineForPostLine);
        horizontalLineForReelLine = findViewById(R.id.horizontalLineForReelLine);

        username = getIntent().getStringExtra("username");
        userName.setText(username);
        currentUserName = getIntent().getStringExtra("currentUserName");

        if(currentUserName.equals(username) && !currentUserName.equals(""))
        {
            Intent i = new Intent(this,MainActivity.class);
            i.putExtra("moveToMyProfile","MyProfile");
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }else {

            mAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
            databaseReference = database.getReference();
            currentUserID = mAuth.getUid();

            DatabaseReference usersRef = databaseReference.child("Users");
            usersRef.orderByChild("name").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            userID = userSnapshot.getKey();

                            userProfileImageURL = userSnapshot.child("profileImage").getValue(String.class);
                            Picasso.get().load(userProfileImageURL).into(userProfile);

                            LoadUserData loadUserData = new LoadUserData(userID);
                            getUserId = loadUserData.getUserID();
                            loadUserData.totalFollowers();
                            loadUserData.totalFollowing();
                            loadUserData.totalPosts();
                            loadUserData.checkFollowing();
                            loadUserData.displayBio();
                            loadUserData.displayPosts();
                            loadUserData.displayReels();
                            loadUserData.checkFollowRequested();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            followers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(followUser.getText().equals("Following"))
                    {
                        if(followers.getText().toString().equals("0"))
                        {
                            Toast.makeText(getApplicationContext(),"No followers",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Intent i = new Intent(viewUserProfile.this, viewUserActivity.class);
                            i.putExtra("activity", "viewUserFollowers");
                            i.putExtra("userID", getUserId);
                            startActivity(i);
                        }
                    }
                    else
                    {
                        DatabaseReference usersRef = databaseReference.child("Users").child(getUserId).child("status");
                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    String status = dataSnapshot.getValue(String.class);
                                    if(status.equals("public"))
                                    {
                                        if(followers.getText().toString().equals("0"))
                                        {
                                            Toast.makeText(getApplicationContext(),"No followers",Toast.LENGTH_LONG).show();
                                        }
                                        else
                                        {
                                            Intent i = new Intent(viewUserProfile.this, viewUserActivity.class);
                                            i.putExtra("activity", "viewUserFollowers");
                                            i.putExtra("userID", getUserId);
                                            startActivity(i);
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
            });

            followerInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(followUser.getText().equals("Following"))
                    {
                        if(followers.getText().toString().equals("0"))
                        {
                            Toast.makeText(getApplicationContext(),"No followers",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Intent i = new Intent(viewUserProfile.this, viewUserActivity.class);
                            i.putExtra("activity", "viewUserFollowers");
                            i.putExtra("userID", getUserId);
                            startActivity(i);
                        }
                    }
                    else
                    {
                        DatabaseReference usersRef = databaseReference.child("Users").child(getUserId).child("status");
                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    String status = dataSnapshot.getValue(String.class);
                                    if(status.equals("public"))
                                    {
                                        if(followers.getText().toString().equals("0"))
                                        {
                                            Toast.makeText(getApplicationContext(),"No followers",Toast.LENGTH_LONG).show();
                                        }
                                        else
                                        {
                                            Intent i = new Intent(viewUserProfile.this, viewUserActivity.class);
                                            i.putExtra("activity", "viewUserFollowers");
                                            i.putExtra("userID", getUserId);
                                            startActivity(i);
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
            });

            following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(followUser.getText().equals("Following"))
                    {
                        if(following.getText().toString().equals("0"))
                        {
                            Toast.makeText(getApplicationContext(),"No following",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Intent i = new Intent(viewUserProfile.this, viewUserActivity.class);
                            i.putExtra("activity", "viewUserFollowing");
                            i.putExtra("userID", getUserId);
                            startActivity(i);
                        }
                    }
                    else
                    {
                        DatabaseReference usersRef = databaseReference.child("Users").child(getUserId).child("status");
                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    String status = dataSnapshot.getValue(String.class);
                                    if(status.equals("public"))
                                    {
                                        if(following.getText().toString().equals("0"))
                                        {
                                            Toast.makeText(getApplicationContext(),"No following",Toast.LENGTH_LONG).show();
                                        }
                                        else
                                        {
                                            Intent i = new Intent(viewUserProfile.this, viewUserActivity.class);
                                            i.putExtra("activity", "viewUserFollowing");
                                            i.putExtra("userID", getUserId);
                                            startActivity(i);
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
            });

            followingInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(followUser.getText().equals("Following"))
                    {
                        if(following.getText().toString().equals("0"))
                        {
                            Toast.makeText(getApplicationContext(),"No following",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Intent i = new Intent(viewUserProfile.this, viewUserActivity.class);
                            i.putExtra("activity", "viewUserFollowing");
                            i.putExtra("userID", getUserId);
                            startActivity(i);
                        }
                    }
                    else
                    {
                        DatabaseReference usersRef = databaseReference.child("Users").child(getUserId).child("status");
                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    String status = dataSnapshot.getValue(String.class);
                                    if(status.equals("public"))
                                    {
                                        if(following.getText().toString().equals("0"))
                                        {
                                            Toast.makeText(getApplicationContext(),"No following",Toast.LENGTH_LONG).show();
                                        }
                                        else
                                        {
                                            Intent i = new Intent(viewUserProfile.this, viewUserActivity.class);
                                            i.putExtra("activity", "viewUserFollowing");
                                            i.putExtra("userID", getUserId);
                                            startActivity(i);
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
            });
        }
    }

    public void showPost(View view) {
        horizontalLineForPostLine.setBackgroundColor(Color.BLACK);
        horizontalLineForReelLine.setBackgroundColor(Color.GRAY);
        if(gridView != null)
            gridView.setVisibility(View.VISIBLE);

        reelGridLayout.setVisibility(View.GONE);
    }

    public void showReels(View view) {
        horizontalLineForReelLine.setBackgroundColor(Color.BLACK);
        horizontalLineForPostLine.setBackgroundColor(Color.GRAY);
        if(gridView != null)
            gridView.setVisibility(View.GONE);

        reelGridLayout.setVisibility(View.VISIBLE);
    }

    class LoadUserData
    {
        private String userID;
        public LoadUserData(String userID)
        {
            this.userID = userID;
        }
        public String getUserID()
        {
            return userID;
        }
        public void totalFollowers()
        {
            DatabaseReference followerRef = databaseReference.child("followers");
            followerRef.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        long countTotalFollowers = dataSnapshot.getChildrenCount();
                        followers.setText(Long.toString(countTotalFollowers));
                    }
                    else
                    {
                        followers.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void totalFollowing()
        {
            DatabaseReference followingRef = databaseReference.child("following");
            followingRef.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        long countTotalFollowing = dataSnapshot.getChildrenCount();
                        following.setText(Long.toString(countTotalFollowing));
                    }
                    else
                    {
                        following.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void totalPosts()
        {
            DatabaseReference postRef = databaseReference.child("posts");
            postRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        long countTotalPosts = dataSnapshot.getChildrenCount();
                        posts.setText(Long.toString(countTotalPosts));
                    }
                    else
                    {
                        posts.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void displayPosts()
        {
            DatabaseReference followersRef = databaseReference.child("followers").child(getUserId).child(mAuth.getUid());
            followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        gridView = findViewById(R.id.imageGridLayout);
                        gridAdapter = new ImageGridAdapter(viewUserProfile.this,getUserId);
                        gridView.setAdapter(gridAdapter);

                        DatabaseReference displayPost = databaseReference.child("posts").child(getUserID());
                        displayPost.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    List<String> pathsId = new ArrayList<>();
                                    List<String> paths = new ArrayList<>();
                                    //Log.d("USERIDFORGLOBALPOST",dataSnapshot.getKey());
                                    for (DataSnapshot getPost: dataSnapshot.getChildren())
                                    {
                                        String pathId = getPost.getKey();
                                        pathsId.add(pathId);

                                        String path = getPost.child("imageURL").getValue(String.class);
                                        paths.add(path);

                                    }

                                    //reversing order to display posts
                                    Collections.reverse(paths);
                                    for(String path : paths)
                                    {
                                        gridAdapter.addImageUri(path);
                                    }

                                    //reversing order of path ids
                                    Collections.reverse(pathsId);
                                    for(String pathId : pathsId)
                                    {
                                        gridAdapter.addPostIds(pathId);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else
                    {
                        DatabaseReference usersRef = databaseReference.child("Users").child(getUserID()).child("status");
                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    String status = dataSnapshot.getValue(String.class);
                                    if(status.equals("public"))
                                    {
                                        gridView = findViewById(R.id.imageGridLayout);
                                        gridAdapter = new ImageGridAdapter(viewUserProfile.this,getUserId);
                                        gridView.setAdapter(gridAdapter);

                                        DatabaseReference displayPost = databaseReference.child("posts").child(getUserID());
                                        displayPost.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists())
                                                {
                                                    List<String> pathsId = new ArrayList<>();
                                                    List<String> paths = new ArrayList<>();
                                                    for (DataSnapshot getPost: dataSnapshot.getChildren())
                                                    {
                                                        String pathId = getPost.getKey();
                                                        pathsId.add(pathId);

                                                        String path = getPost.child("imageURL").getValue(String.class);
                                                        paths.add(path);
                                                    }

                                                    //reversing order to display posts
                                                    Collections.reverse(paths);
                                                    for(String path : paths)
                                                    {
                                                        gridAdapter.addImageUri(path);
                                                    }

                                                    //reversing order of path ids
                                                    Collections.reverse(pathsId);
                                                    for(String pathId : pathsId)
                                                    {
                                                        gridAdapter.addPostIds(pathId);
                                                    }

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
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
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void displayReels(){

            DatabaseReference followersRef = databaseReference.child("followers").child(getUserId).child(mAuth.getUid());
            followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        // adpater for reel
                        reelGridLayout = findViewById(R.id.reelGridLayout);
                        reelAdapter = new ReelAdapter(viewUserProfile.this,getUserId,username,userProfileImageURL,currentUserName);
                        reelGridLayout.setAdapter(reelAdapter);

                        DatabaseReference displayReel = databaseReference.child("reels").child(getUserID());
                        displayReel.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    List<String> pathsId = new ArrayList<>();
                                    List<String> paths = new ArrayList<>();
                                    List<String> captions = new ArrayList<>();
                                    //Log.d("USERIDFORGLOBALPOST",dataSnapshot.getKey());
                                    for (DataSnapshot getReel: dataSnapshot.getChildren())
                                    {
                                        String pathId = getReel.getKey();
                                        pathsId.add(pathId);

                                        String path = getReel.child("videoURL").getValue(String.class);
                                        paths.add(path);

                                        String videoCaption = getReel.child("caption").getValue(String.class);
                                        captions.add(videoCaption);

                                    }

                                    //reversing order to display posts
                                    Collections.reverse(paths);
                                    for(String path : paths)
                                    {
                                        reelAdapter.addVideoUrl(path);
                                    }

                                    //reversing order of path ids
                                    Collections.reverse(pathsId);
                                    for(String pathId : pathsId)
                                    {
                                        reelAdapter.addPostIds(pathId);
                                    }

                                    //reversing order to display captions
                                    Collections.reverse(captions);
                                    for(String videoCaption : captions)
                                    {
                                        reelAdapter.addCaptions(videoCaption);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else
                    {
                        DatabaseReference usersRef = databaseReference.child("Users").child(getUserID()).child("status");
                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    String status = dataSnapshot.getValue(String.class);
                                    if(status.equals("public"))
                                    {
                                        // adpater for reel
                                        reelGridLayout = findViewById(R.id.reelGridLayout);
                                        reelAdapter = new ReelAdapter(viewUserProfile.this,getUserId,username,userProfileImageURL,currentUserName);
                                        reelGridLayout.setAdapter(reelAdapter);

                                        DatabaseReference displayReel = databaseReference.child("reels").child(getUserID());
                                        displayReel.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists())
                                                {
                                                    List<String> pathsId = new ArrayList<>();
                                                    List<String> paths = new ArrayList<>();
                                                    List<String> captions = new ArrayList<>();
                                                    for (DataSnapshot getReel: dataSnapshot.getChildren())
                                                    {
                                                        String pathId = getReel.getKey();
                                                        pathsId.add(pathId);

                                                        String path = getReel.child("videoURL").getValue(String.class);
                                                        paths.add(path);

                                                        String videoCaption = getReel.child("caption").getValue(String.class);
                                                        captions.add(videoCaption);

                                                    }

                                                    //reversing order to display posts
                                                    Collections.reverse(paths);
                                                    for(String path : paths)
                                                    {
                                                        reelAdapter.addVideoUrl(path);
                                                    }

                                                    //reversing order of path ids
                                                    Collections.reverse(pathsId);
                                                    for(String pathId : pathsId)
                                                    {
                                                        reelAdapter.addPostIds(pathId);
                                                    }

                                                    //reversing order to display captions
                                                    Collections.reverse(captions);
                                                    for(String videoCaption : captions)
                                                    {
                                                        reelAdapter.addCaptions(videoCaption);
                                                    }

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    else{

                                        reelGridLayout = findViewById(R.id.reelGridLayout);


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
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        public void displayBio()
        {
            DatabaseReference loadUserBio = databaseReference.child("Users");
            loadUserBio.child(getUserID()).child("bio").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String Bio = dataSnapshot.getValue(String.class);
                        if(Bio.isEmpty() || Bio.trim().isEmpty())
                            userBIO.setText("");
                        else
                            userBIO.setText(Bio);
                    } else {
                        // Handle the case where the data doesn't exist
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle potential errors here
                }
            });
        }

        public void checkFollowing() {

            DatabaseReference followerRef = databaseReference.child("followers");
            followerRef.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        for(DataSnapshot checkFollowing : dataSnapshot.getChildren())
                        {
                            String uid = checkFollowing.getKey();

                            if(uid.equals(currentUserID))
                            {
                                followUser.setText("Following");
                                followUser.setBackgroundColor(Color.parseColor("#F1F8E9"));
                                followUser.setTextColor(Color.BLACK);
                                //followUser.setOnClickListener(null);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void checkFollowRequested() {
            DatabaseReference checkFollowRequested = database.getReference("requests").child(getUserId).child(currentUserID);
            checkFollowRequested.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        followUser.setText("Requested");
                        followUser.setBackgroundColor(Color.parseColor("#F1F8E9"));
                        followUser.setTextColor(Color.BLACK);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    public void goBack(View view) {

        String moveToHome= getIntent().getStringExtra("moveToHomeFragment");
        if(moveToHome!=null)
        {
            Intent i = new Intent(viewUserProfile.this,MainActivity.class);
            i.putExtra("moveToHomeFragment","true");

            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        else
        {
            Intent i = new Intent(viewUserProfile.this,MainActivity.class);
            i.putExtra("moveToSearchFragment","true");

            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }


    }

    public void followUser(View view) {

        if(followUser.getText().equals("Requested"))
        {
            DatabaseReference removeFollowRequest = database.getReference("requests").child(getUserId).child(currentUserID);
            removeFollowRequest.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    followUser.setText("Follow");
                    followUser.setBackgroundColor(Color.parseColor("#4169e1"));
                    followUser.setTextColor(Color.WHITE);
                }
            });
        }
        else if(followUser.getText().equals("Following"))
        {
            String username = userName.getText().toString();
            AlertDialog.Builder builder = new AlertDialog.Builder(viewUserProfile.this);
            builder.setTitle("Are you Sure?");
            TextView message = new TextView(viewUserProfile.this);
            message.setText("\n" + "Unfollow " + username);
            message.setTextColor(Color.RED);
            message.setTextSize(20);
            message.setTypeface(null, Typeface.BOLD);
            message.setGravity(Gravity.CENTER);
            builder.setView(message);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseReference followersRef = databaseReference.child("followers").child(getUserId).child(currentUserID);
                    followersRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                DatabaseReference followingRef = databaseReference.child("following").child(currentUserID).child(getUserId);
                                followingRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            followUser.setText("Follow");
                                            followUser.setBackgroundColor(Color.parseColor("#4169e1"));
                                            followUser.setTextColor(Color.WHITE);
                                        }
                                    }
                                });
                            }
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
        else
        {
            DatabaseReference checkPrivateAccount = database.getReference("Users").child(getUserId).child("status");
            checkPrivateAccount.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        String status = dataSnapshot.getValue(String.class);
                        if(status.equals("public"))
                        {
                            DatabaseReference followersRef = database.getReference("followers").child(getUserId).child(currentUserID);
                            followersRef.setValue("following");

                            DatabaseReference followingfRef = database.getReference("following").child(currentUserID).child(getUserId);
                            followingfRef.setValue("follows");

                            DatabaseReference followersListenerRef = database.getReference("followers").child(userID);
                            followersListenerRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    if(!isFollowersUpdated)
                                    {
                                        followUser.setText("Following");
                                        followUser.setBackgroundColor(Color.parseColor("#F1F8E9"));
                                        followUser.setTextColor(Color.BLACK);
                                        //followUser.setOnClickListener(null);

                                        //int getFollwers = Integer.parseInt(followers.getText().toString());
                                        //getFollwers += 1;
                                        //followers.setText(String.valueOf(getFollwers));

                                        isFollowersUpdated = true;
                                    }

                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            //Toast.makeText(viewUserProfile.this,"private account",Toast.LENGTH_LONG).show();
                            DatabaseReference followRequest = database.getReference("requests").child(getUserId).child(currentUserID);
                            followRequest.setValue("requested");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }
}