package com.example.sessionexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sessionexample.adapter.RecyclerViewAdapter;
import com.example.sessionexample.adapter.StoryAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements StoryAdapter.ImagePickListener {

    private RecyclerView recyclerView;

    //for story starts
    private RecyclerView storiesBar;
    ArrayList<String> storySource;
    LinearLayoutManager linearLayoutManager;
    StoryAdapter storyAdapter;

    //for story ends
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<PostFeed> postFeeds;
    private DatabaseReference postsReference;
    private DatabaseReference usersReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        storiesBar=view.findViewById(R.id.storyList);
        recyclerView = view.findViewById(R.id.postList);
        storySource=new ArrayList<>();
        storySource.add("forself");//for self
        linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        storyAdapter=new StoryAdapter(getContext(),storySource,this);
        storiesBar.setLayoutManager(linearLayoutManager);
        storiesBar.setAdapter(storyAdapter);



        String user=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase storydatabase = FirebaseDatabase.getInstance();
        DatabaseReference followingRef = storydatabase.getReference("following");
        DatabaseReference storyRef=storydatabase.getReference("story");

        String targetValue = "follows";

        followingRef.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Iterate through the nested keys and check if the value is "follows"
                for (DataSnapshot nestedSnapshot : dataSnapshot.getChildren()) {
                    String nestedKey = nestedSnapshot.getKey();
                    String nestedValue = nestedSnapshot.getValue(String.class);
                    // Toast.makeText(getContext(), nestedKey, Toast.LENGTH_SHORT).show();
                    checkStory(new CheckFollowingStoryListener() {
                        @Override
                        public void onStoryChecked(boolean storyExists) {
                            if(storyExists)
                            {
                                storySource.add(nestedKey);
                                storyAdapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onStoryCheckFailed(DatabaseError databaseError) {

                        }
                    },nestedKey);



                    if (nestedValue != null && nestedValue.equals(targetValue)) {
                        System.out.println("User key: " + user + ", Nested key: " + nestedKey);
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getMessage());
            }
        });



        // Initialize the ArrayList
        postFeeds = new ArrayList<>();

        // Set up the RecyclerView and its adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), postFeeds);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Firebase setup
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        postsReference = database.getReference("posts");
        usersReference = database.getReference("Users");

        // Fetching data from Firebase
        postsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postFeeds.clear(); // Clear the existing list
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String userNameKey = postSnapshot.getKey();

                    // Fetch the user name before using it in the loop
                    DatabaseReference specificUserReference = usersReference.child(userNameKey);
                    specificUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            if (userSnapshot.exists()) {
                                String userName = userSnapshot.child("name").getValue(String.class);
                                String profileImageurl = userSnapshot.child("profileImage").getValue(String.class);
                                Log.d("image",profileImageurl);
                                for (DataSnapshot post : postSnapshot.getChildren()) {
                                    PostFeed postFeed = post.getValue(PostFeed.class);
                                    postFeed.setPostUsername(userNameKey);
                                    postFeed.setProfileImage(profileImageurl);
                                    postFeed.setPostId(post.getKey());
                                    postFeed.setUserName(userName);
                                    postFeeds.add(postFeed);
                                }

                                recyclerViewAdapter.notifyDataSetChanged(); // Notify adapter
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors
                            Toast.makeText(getContext(), "Failed to fetch username.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors or failure to retrieve data
                Toast.makeText(getContext(), "Failed to fetch data.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onImagePicked() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imageActivityResultLauncher.launch(intent);
    }

    @Override
    public void viewStory(String userKey) {
        Intent intent = new Intent(getContext(), ViewStory.class);

        // Optionally, you can pass data to the new activity
        intent.putExtra("userkey", userKey);

        // Start the new activity
        startActivity(intent);
    }

    private ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    // Handle the selected image data here
                    handlePickedImage(data, getContext());

                }
            }
    );

    private void handlePickedImage(Intent data, Context context) {
        Uri imageUri = data.getData();
        Intent intent = new Intent(context, UploadStoryActivity.class);
        intent.putExtra("IMAGE_URI", imageUri.toString());
        context.startActivity(intent);
    }

    public void checkStory(CheckFollowingStoryListener listener,String userNameKey) {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference storyReference = database.getReference("story");
        DatabaseReference specificUserReference = storyReference.child(userNameKey);
        specificUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                        String timestampString = dataSnapshot1.child("storyUploadTimestamp").getValue(String.class);
                        //Toast.makeText(getContext(), timestampString, Toast.LENGTH_SHORT).show();
                        long intervalMillis = 24 * 60 * 60 * 1000;
                        long timestamp=Long.parseLong(timestampString);
                        long currenttime=System.currentTimeMillis();
                        if(currenttime-timestamp>intervalMillis)
                        {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("story/"+userNameKey
                            +"/"+dataSnapshot1.getKey());

// Use removeValue() to delete the data
                            databaseReference.removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        // Deletion successful
                                        Log.d("Firebase Database", "Data deleted successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle any errors
                                        Log.e("Firebase Database", "Error deleting data: " + e.getMessage());
                                    });
                            // Assume you have a Firebase Storage reference pointing to the file you want to delete
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("story"+timestampString+"_"+userNameKey);

// Use delete() to delete the file
                            storageReference.delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Deletion successful
                                        Log.d("Firebase Storage", "File deleted successfully");
                                    })
                                    .addOnFailureListener(exception -> {
                                        // Handle any errors
                                        Log.e("Firebase Storage", "Error deleting file: " + exception.getMessage());
                                    });

                        }
                        else{
                            Toast.makeText(getContext(), "story/"+timestampString+"_"+userNameKey, Toast.LENGTH_SHORT).show();
                            listener.onStoryChecked(true);
                        }
                    }

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
    public interface CheckFollowingStoryListener {
        void onStoryChecked(boolean storyExists);
        void onStoryCheckFailed(DatabaseError databaseError);
    }
}
