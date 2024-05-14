package com.example.sessionexample;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class globalPosts extends Fragment {
    private GlobalPostsAdapter globalPosts;
    private static final int POST_DETAIL_REQUEST_CODE = 1;
    GridView gridView;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    List<String> myPosts,myPostsIds;
    public globalPosts() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == POST_DETAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            // Perform the refresh or update operation here
            if (globalPosts != null) {
                globalPosts.clearImageUris();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_global_posts, container, false);
        EditText search = view.findViewById(R.id.searchUser);
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    Intent i = new Intent(getActivity(),MainActivity.class);
                    i.putExtra("moveToSearchFragment","moveToSearchFragment");
                    startActivity(i);
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),MainActivity.class);
                i.putExtra("moveToSearchFragment","moveToSearchFragment");
                startActivity(i);
            }
        });

        gridView = view.findViewById(R.id.imageGridLayout);
        globalPosts = new GlobalPostsAdapter(getContext());
        gridView.setAdapter(globalPosts);

        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();

        getMyAllPosts();

        DatabaseReference displayPost = databaseReference.child("posts");
        displayPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    List<String> paths = new ArrayList<>();
                    List<String> pathsId = new ArrayList<>();
                    for (DataSnapshot getUserId: dataSnapshot.getChildren())
                    {
                        Log.d("userid",getUserId.getKey());
                        for(DataSnapshot getPost : getUserId.getChildren())
                        {
                            Log.d("postid",getPost.getKey());
                            String path = getPost.child("imageURL").getValue(String.class);
                            paths.add(path);
                            pathsId.add(getPost.getKey());
                        }
                    }

                    for(String myPost : myPosts)
                    {
                        for(String globalPost : paths)
                        {
                            if(myPost.equals(globalPost))
                            {
                                paths.remove(myPost);
                                break;
                            }
                        }
                    }

                    for(String myPostId : myPostsIds)
                    {
                        for(String globalPostsId : pathsId)
                        {
                            if(myPostId.equals(globalPostsId))
                            {
                                pathsId.remove(myPostId);
                                break;
                            }
                        }
                    }


                    //reversing order to display posts
                    Collections.reverse(paths);
                    for(String path : paths)
                    {
                        globalPosts.addImageUri(path);
                    }

                    Collections.reverse(pathsId);
                    for(String pathId : pathsId)
                    {
                        globalPosts.setPostIdForGlobalPosts(pathId);
                    }

                    globalPosts.randomDisplayPosts();
                    //globalPosts.randomDisplayPostsId();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }


    private void getMyAllPosts()
    {
        myPosts = new ArrayList<>();
        myPostsIds = new ArrayList<>();
        DatabaseReference myPostsRef = databaseReference.child("posts").child(mAuth.getUid());
        myPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot postIds : dataSnapshot.getChildren())
                    {
                        myPostsIds.add(postIds.getKey());
                        Log.d("myPostId",postIds.getKey());
                        myPosts.add(postIds.child("imageURL").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}