package com.example.sessionexample;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlobalPostsAdapter extends BaseAdapter {
    private Context context;
    private List<String> userIDs,globalPostIds,uniquePostIdList;
    public List<String> imageUris;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    public GlobalPostsAdapter(Context context)
    {
        this.context = context;
        imageUris = new ArrayList<>();
        userIDs = new ArrayList<>();
        globalPostIds = new ArrayList<>();
        uniquePostIdList = new ArrayList<>();
        database = FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();
    }

    @Override
    public int getCount() {
        return imageUris.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardView cardView;
        if (convertView == null) {
            cardView = (CardView) LayoutInflater.from(context).inflate(R.layout.global_item_posts, parent, false);
        } else {
            cardView = (CardView) convertView;
        }

        ImageView imageView = cardView.findViewById(R.id.gridImageView);
        Picasso.get().load(imageUris.get(position)).into(imageView);

        Log.d("globalpostid", String.valueOf(globalPostIds.size()));


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference getUserId = databaseReference.child("posts");
                getUserId.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot userSnapShot : dataSnapshot.getChildren())
                        {
                            for(DataSnapshot postSnapShot : userSnapShot.getChildren())
                            {
                                String imageURL = postSnapShot.child("imageURL").getValue(String.class);
                                if(imageURL.equals(imageUris.get(position)))
                                {
                                    String userId = userSnapShot.getKey();
                                    Intent intent = new Intent(context, PostDetailScrollActivity.class);
                                    intent.putExtra("firstImage",imageUris.get(position));
                                    intent.putExtra("firstImageUserId",userId);
                                    intent.putStringArrayListExtra("globalpostids", (ArrayList<String>) globalPostIds);
                                    context.startActivity(intent);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        return cardView;
    }
    public void addImageUri(String imageUri) {
        imageUris.add(imageUri);
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }
    public void setPostIdForGlobalPosts(String postid)
    {
        globalPostIds.add(postid);
    }

    public void randomDisplayPosts() {
        // Create a list of indices
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < imageUris.size(); i++) {
            indices.add(i);
        }

        // Shuffle the list of indices
        Collections.shuffle(indices);

        // Create temporary lists to hold shuffled values
        List<String> shuffledImageUris = new ArrayList<>();
        List<String> shuffledGlobalPostIds = new ArrayList<>();

        // Rearrange the lists based on shuffled indices
        for (int index : indices) {
            Log.d("indexofindices", String.valueOf(index));
            shuffledImageUris.add(imageUris.get(index));
            shuffledGlobalPostIds.add(globalPostIds.get(index));
        }

        // Update the original lists with shuffled values
        imageUris = shuffledImageUris;
        globalPostIds = shuffledGlobalPostIds;

        for(String imagePath : imageUris)
            Log.d("randomImage",imagePath);

        for(String imagePathId : globalPostIds)
            Log.d("randomImageId",imagePathId);


        notifyDataSetChanged();
    }

    public void clearImageUris()
    {
        imageUris.clear();
        notifyDataSetChanged();
    }

}
