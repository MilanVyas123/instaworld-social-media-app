package com.example.sessionexample;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sessionexample.reels.ReelAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MyProfile extends Fragment {
    Button logout,editProfile,followRequest;
    String email,id,imageUri;
    TextView username,userBIO,followerInfo,followingInfo;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseUser user;
    ImageView addPost,profileImage,setting,showPost,showReels;
    StorageReference storageReference;
    TextView following,followers,posts;
    GridView gridView,reelGridLayout;
    ImageGridAdapter gridAdapter;
    ReelAdapter reelAdapter;
    View showingPosts,showingReels;

    public MyProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        gridView = view.findViewById(R.id.imageGridLayout);
        gridAdapter = new ImageGridAdapter(getContext(),mAuth.getUid());
        gridView.setAdapter(gridAdapter);

        username = view.findViewById(R.id.nameTextView);
        following = view.findViewById(R.id.followingCount);
        followingInfo = view.findViewById(R.id.followingInfo);
        followers = view.findViewById(R.id.followerCount);
        followerInfo = view.findViewById(R.id.followerInfo);
        posts = view.findViewById(R.id.postCount);
        showPost = view.findViewById(R.id.allPostsGrid);
        showReels = view.findViewById(R.id.allReelsGrid);
        showingPosts = view.findViewById(R.id.horizontalLineForPostLine);
        showingReels = view.findViewById(R.id.horizontalLineForReelLine);
        followRequest = view.findViewById(R.id.followRequest);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();

        String uid = mAuth.getUid();
        userBIO =view.findViewById(R.id.userBIO);
        profileImage = view.findViewById(R.id.profileImage);

        DatabaseReference usersRef = databaseReference.child("Users").child(uid);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    // Now you can use the user's name
                    username.setText(userName);

                    imageUri = dataSnapshot.child("profileImage").getValue(String.class);

                    String bio = dataSnapshot.child("bio").getValue(String.class);
                    if(bio != "" && bio != null){
                        userBIO.setText(bio.toString());
                    }

                    Picasso.get().load(imageUri).into(profileImage);

                    // adpater for reel
                    reelGridLayout = view.findViewById(R.id.reelGridLayout);
                    reelAdapter = new ReelAdapter(getContext(),mAuth.getUid(),userName,imageUri,userName);
                    reelGridLayout.setAdapter(reelAdapter);

                } else {
                    // Handle the case where the data doesn't exist
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
            }
        });

        DatabaseReference displayPost = databaseReference.child("posts").child(uid);
        displayPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    List<String> paths = new ArrayList<>();
                    List<String> postIDs = new ArrayList<>();
                    for (DataSnapshot getPost: dataSnapshot.getChildren())
                    {
                        //Log.d("POSTID",getPost.getKey());
                        postIDs.add(getPost.getKey());
                        String path = getPost.child("imageURL").getValue(String.class);
                        paths.add(path);
                    }

                    //reversing order to display posts
                    Collections.reverse(paths);
                    for(String path : paths)
                    {
                        gridAdapter.addImageUri(path);
                    }

                    //reversing order to display postsids
                    Collections.reverse(postIDs);
                    for(String postid : postIDs)
                    {
                        gridAdapter.addPostIds(postid);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference displayReel = databaseReference.child("reels").child(uid);
        displayReel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    List<String> paths = new ArrayList<>();
                    List<String> ReelIDs = new ArrayList<>();
                    List<String> captions = new ArrayList<>();
                    for (DataSnapshot getReel: dataSnapshot.getChildren())
                    {
                        //Log.d("POSTID",getPost.getKey());
                        ReelIDs.add(getReel.getKey());
                        String path = getReel.child("videoURL").getValue(String.class);
                        paths.add(path);

                        String videoCaption = getReel.child("caption").getValue(String.class);
                        captions.add(videoCaption);

                    }

                    //reversing order to display reels
                    Collections.reverse(paths);
                    for(String path : paths)
                    {
                        reelAdapter.addVideoUrl(path);
                    }

                    //reversing order to display reelids
                    Collections.reverse(ReelIDs);
                    for(String postid : ReelIDs)
                    {
                        reelAdapter.addPostIds(postid);
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


        usersRef.child(uid).child("bio").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String Bio = dataSnapshot.getValue(String.class);

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

        /*usersRef.child(uid).child("profileImage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    imageUri = dataSnapshot.getValue(String.class);

                    Picasso.get().load(imageUri).into(profileImage);

                } else {
                    // Handle the case where the data doesn't exist
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
            }
        }); */

        if(user != null)
        {
            id = user.getUid();

            usersRef = databaseReference.child("Users");
            usersRef.child(id).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userName = dataSnapshot.getValue(String.class);
                    username.setText(userName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        DatabaseReference followingRef = databaseReference.child("following");
        followingRef.child(uid).addValueEventListener(new ValueEventListener() {
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

        DatabaseReference followerRef = databaseReference.child("followers");
        followerRef.child(uid).addValueEventListener(new ValueEventListener() {
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

        DatabaseReference postRef = databaseReference.child("posts");
        postRef.child(uid).addValueEventListener(new ValueEventListener() {
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

        addPost = view.findViewById(R.id.add);

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("moveToAddPostFragment","moveToAddPostFragment");
                startActivity(intent);
            }
        });

        editProfile = view.findViewById(R.id.editProfile);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfile.class);
                startActivity(intent);
            }
        });

        setting = view.findViewById(R.id.setting);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Setting.class);
                intent.putExtra("username",username.getText().toString());
                startActivity(intent);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(followers.getText().toString().equals("0"))
                {
                    Toast.makeText(getActivity(),"No Followers",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent i = new Intent(getActivity(), viewUserActivity.class);
                    i.putExtra("activity", "viewUserFollowers");
                    i.putExtra("userID", uid);
                    startActivity(i);
                }
            }
        });

        followerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(followers.getText().toString().equals("0"))
                {
                    Toast.makeText(getActivity(),"No Followers",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent i = new Intent(getActivity(), viewUserActivity.class);
                    i.putExtra("activity", "viewUserFollowers");
                    i.putExtra("userID", uid);
                    startActivity(i);
                }
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(following.getText().toString().equals("0"))
                {
                    Toast.makeText(getActivity(),"No Following",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent i = new Intent(getActivity(), viewUserActivity.class);
                    i.putExtra("activity", "viewUserFollowing");
                    i.putExtra("userID", uid);
                    startActivity(i);
                }
            }
        });

        followingInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(following.getText().toString().equals("0"))
                {
                    Toast.makeText(getActivity(),"No Following",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent i = new Intent(getActivity(), viewUserActivity.class);
                    i.putExtra("activity", "viewUserFollowing");
                    i.putExtra("userID", uid);
                    startActivity(i);
                }
            }
        });

        showPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showingPosts.setBackgroundColor(Color.BLACK);
                showingReels.setBackgroundColor(Color.GRAY);
                gridView.setVisibility(View.VISIBLE);
                reelGridLayout.setVisibility(View.GONE);
            }
        });

        showReels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showingPosts.setBackgroundColor(Color.GRAY);
                showingReels.setBackgroundColor(Color.BLACK);
                gridView.setVisibility(View.GONE);
                reelGridLayout.setVisibility(View.VISIBLE);
            }
        });

        followRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ViewFollowRequest.class);
                startActivity(i);
            }
        });

        return view;
    }
}