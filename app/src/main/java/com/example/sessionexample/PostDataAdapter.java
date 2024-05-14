package com.example.sessionexample;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sessionexample.adapter.RecyclerViewAdapter;
import com.example.sessionexample.comment.CommentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDataAdapter extends RecyclerView.Adapter<PostDataAdapter.MyViewHolder> {
    private Context context;
    private String imagePath,userId,firstImage,firstImageUserId,currentUserName,firstPostId;
    private List<String> imageUris,userIDs,allPostsURL,myPosts,postIds,globalPostIds,myPostIds,newGlobalPostIds;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    File directory;

    //defining username for displayling username in textview
    String username;
    private void getFirstPostId(String firstImage) {

        DatabaseReference postsRef = databaseReference.child("posts");
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    outerLoop:for(DataSnapshot userId : dataSnapshot.getChildren()){
                        for(DataSnapshot postIdref : userId.getChildren()){
                            String postId = postIdref.getKey();
                            Log.d("Post id : " , postId);
                            String imageURL = postIdref.child("imageURL").getValue(String.class);
                            if(imageURL.equals(firstImage)){
                                firstPostId = postId;
                                break outerLoop;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public PostDataAdapter(Context context ,List<String> imageUris,List<String> postIds,String userId)
    {
        //constructor for my profile

        this.context = context;
        //this.imagePath = imagePath;
        this.imageUris = imageUris;
        this.postIds = postIds;
        this.userId = userId;

        // for my profile only post id will display
        displayPostIds();


        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();

    }

    private void displayPostIds()
    {
        for(String postid : postIds)
        {
            Log.d("POSTIDFROMPOSTDATAADAPTERCLASS",postid);
        }
    }

    public PostDataAdapter(Context context,String firstImage,String firstImageUserId,List<String> globalPostIds)
    {
        // constructor for global posts

        this.context = context;
        this.firstImage = firstImage;
        this.firstImageUserId = firstImageUserId;
        this.globalPostIds = globalPostIds;

        displayGlobalPostIds();

        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();

        getFirstPostId(firstImage);

        getMyAllPosts();
        getAllPosts();
        getCurrentUserName();
    }

    private void displayGlobalPostIds()
    {
        for(String postid : globalPostIds)
        {
            Log.d("GLOBALPOSTIDFROMPOSTDATAADAPTER",postid);
        }

    }

    private void getCurrentUserName()
    {
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

    private void getMyAllPosts()
    {
        myPosts = new ArrayList<>();
        myPostIds = new ArrayList<>();
        DatabaseReference myPostsRef = databaseReference.child("posts").child(mAuth.getUid());
        myPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot postIds : dataSnapshot.getChildren())
                    {
                        myPostIds.add(postIds.getKey());
                        myPosts.add(postIds.child("imageURL").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllPosts() {
        allPostsURL = new ArrayList<>();
        newGlobalPostIds = new ArrayList<>();
        allPostsURL.clear();
        DatabaseReference getPosts = databaseReference.child("posts");
        getPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userIdSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot postIdSnapshot : userIdSnapshot.getChildren()) {
                            newGlobalPostIds.add(postIdSnapshot.getKey());
                            String imagePath = postIdSnapshot.child("imageURL").getValue(String.class);
                            allPostsURL.add(imagePath);
                        }
                    }

                    for(String myPost : myPosts)
                    {
                        for(String globalPost : allPostsURL)
                        {
                            if(myPost.equals(globalPost))
                            {
                                allPostsURL.remove(myPost);
                                break;
                            }
                        }
                    }

                    for(String myPostId : myPostIds)
                    {
                        for(String globalPostId : newGlobalPostIds)
                        {
                            if(myPostId.equals(globalPostId))
                            {
                                newGlobalPostIds.remove(myPostId);
                                break;
                            }
                        }
                    }

                    allPostsURL.remove(firstImage);
                    newGlobalPostIds.remove(globalPostIds.get(0));
                    notifyDataSetChanged();

                    List<Integer> indices = new ArrayList<>();
                    for (int i = 0; i < allPostsURL.size(); i++) {
                        indices.add(i);
                    }
                    // Shuffle the list of indices
                    Collections.shuffle(indices);

                    // Create temporary lists to hold shuffled values
                    List<String> shuffledAllPostsURL = new ArrayList<>();
                    List<String> shuffledNewGlobalPostIds = new ArrayList<>();

                    // Rearrange the lists based on shuffled indices
                    for (int index : indices) {
                        shuffledAllPostsURL.add(allPostsURL.get(index));
                        shuffledNewGlobalPostIds.add(newGlobalPostIds.get(index));
                    }

                    allPostsURL = shuffledAllPostsURL;
                    newGlobalPostIds = shuffledNewGlobalPostIds;

                    //Collections.shuffle(allPostsURL);
                    notifyDataSetChanged();
                    getUserIDs(allPostsURL);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getUserIDs(List<String> allPostsURL)
    {
        userIDs = new ArrayList<>();
        userIDs.clear();

        for(String imagePath : allPostsURL)
        {
            DatabaseReference allPostsRef = databaseReference.child("posts");
            allPostsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean flag=false;
                    if(dataSnapshot.exists())
                    {
                        for(DataSnapshot usersRef : dataSnapshot.getChildren())
                        {
                            for(DataSnapshot postId : usersRef.getChildren())
                            {
                                String userPostId = postId.child("imageURL").getValue(String.class);
                                if(userPostId.equals(imagePath))
                                {
                                    userIDs.add(usersRef.getKey());
                                    flag=true;
                                }
                                if (flag)
                                    break;
                            }
                            if(flag)
                                break;
                        }
                        notifyDataSetChanged();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    private void deletePostFromFirebaseStorage(String postPath)
    {
        String bucket = extractBucketFromUrl(postPath);
        String path = extractPathFromUrl(postPath);

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://" + bucket + "/" + path);
        storageRef.delete();
    }

    private String extractBucketFromUrl(String postPath)
    {
        // Extract the bucket name from the URL
        int start = postPath.indexOf("/b/") + 3;
        int end = postPath.indexOf("/o/");
        return postPath.substring(start, end);
    }

    private String  extractPathFromUrl(String postPath)
    {
        // Extract the path from the URL
        int start = postPath.indexOf("/o/") + 3;
        int end = postPath.indexOf("?alt=media");
        return postPath.substring(start, end).replace("%2F", "/");
    }

    private void downloadPost(String postUrl) {
        // Create a reference to the video file
        StorageReference storageRef = storage.getReferenceFromUrl(postUrl);

        // Get the download URL
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Start downloading the video using DownloadManager
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(uri);

                // Specify the directory path where you want to save the video
                directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "instaWorld-Images");

                // Create the directory if it doesn't exist
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Set the destination directory for the download request
                request.setDestinationUri(Uri.fromFile(new File(directory, postUrl + ".jpg")));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                downloadManager.enqueue(request);

                // Display a toast message
                Toast.makeText(context, "Image downloading...", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    // Construct the file path to the downloaded video
                    String filePath = new File(directory, postUrl + ".jpg").getPath();

                    // After the download completes successfully
                    MediaScannerConnection.scanFile(context,
                            new String[]{filePath},  // Array of paths to scan
                            null, // MIME types to consider for the media scanner (null for all)
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {

                                    // Scan completed
                                    // You can perform additional actions here if needed
                                }
                            });


                    Toast.makeText(context, "Image downloaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @NonNull
    @Override
    public PostDataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.post_items,parent,false);
        return new PostDataAdapter.MyViewHolder(view);
    }
    public void readData(String path, RecyclerViewAdapter.FirebaseCallback callback) {
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

    @Override
    public void onBindViewHolder(@NonNull PostDataAdapter.MyViewHolder holder, int position) {

        Animation zoomInAnim = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
        Animation zoomOutAnim = AnimationUtils.loadAnimation(context, R.anim.zoom_out);

        if(firstImage == null && firstImageUserId == null)
        {
            // for SPECIFIC USER PROFILE POSTS

            // likes and comments code for my profile post only
            if(mAuth.getUid().equals(userId))
            {

                PostFeed postFeed=new PostFeed();
                String postId=postIds.get(position);

                readData("likes/"+mAuth.getUid()+"/"+postId, new RecyclerViewAdapter.FirebaseCallback() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        // Handle successful data retrieval here
                        postFeed.setLikesCount((int) dataSnapshot.getChildrenCount());

                        holder.likesCount.setText(postFeed.getLikesCount());
                        if(dataSnapshot.hasChild(mAuth.getUid()))
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
                holder.commentIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, CommentActivity.class);
                        intent.putExtra("postId",postId);
                        intent.putExtra("postUserId",mAuth.getUid());




                        // Start the new activity
                        context.startActivity(intent);

                    }
                });
                holder.likeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("postid", String.valueOf(postFeed.getLiked()));
                        if (postFeed.getLiked()) {
                            holder.likesCount.setText(postFeed.decreaseLike());

                            holder.likeIcon.setImageResource(R.drawable.baseline_favorite_border_24);
                            readData("likes/"+mAuth.getUid()+"/"+postId, new RecyclerViewAdapter.FirebaseCallback() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    DatabaseReference childReference = dataSnapshot.getRef().child(mAuth.getUid());


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
                            Log.d("liked",mAuth.getUid()+" "+postId);
                            String postUserId=mAuth.getUid();

                            DatabaseReference likeRefernce= FirebaseDatabase.getInstance().getReference("likes");
                            likeRefernce.child(postUserId).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.hasChild(mAuth.getUid()))
                                    {
                                        Map<String, Object> postDetails = new HashMap<>();
                                        postDetails.put(mAuth.getUid(),"liked");


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

                holder.imageView.setOnClickListener(new PostDataAdapter.DoubleClickListener() {
                    @Override
                    public void onDoubleClick(View v) {
                        // holder.insideImage.setVisibility(View.VISIBLE);
                        holder.likeIcon.setImageResource(R.drawable.baseline_favorite_24);
                        holder.likeIcon.startAnimation(zoomInAnim);
                        holder.insideImage.startAnimation(zoomInAnim);
                        holder.insideImage.startAnimation(zoomOutAnim);

                        Log.d("liked",mAuth.getUid()+" "+postId);
                        String postUserId=mAuth.getUid();

                        DatabaseReference likeRefernce= FirebaseDatabase.getInstance().getReference("likes");
                        likeRefernce.child(postUserId).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.hasChild(mAuth.getUid()))
                                {
                                    Map<String, Object> postDetails = new HashMap<>();
                                    postDetails.put(mAuth.getUid(),"liked");
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

            }

            else
            {

                PostFeed postFeed=new PostFeed();
                String postId=postIds.get(position);

                holder.commentIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, CommentActivity.class);
                        intent.putExtra("postId",postId);
                        intent.putExtra("postUserId",userId);

                        // Start the new activity
                        context.startActivity(intent);

                    }
                });


                readData("likes/"+userId+"/"+postId, new RecyclerViewAdapter.FirebaseCallback() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        // Handle successful data retrieval here
                        postFeed.setLikesCount((int) dataSnapshot.getChildrenCount());

                        holder.likesCount.setText(postFeed.getLikesCount());
                        if(dataSnapshot.hasChild(mAuth.getUid()))
                        {
                            holder.likeIcon.setImageResource(R.drawable.baseline_favorite_24);
                            postFeed.setLiked(true);
                            Log.d("milan","suc");
                        }
                        else{
                            postFeed.setLiked(false);
                            Log.d("milan","suc1");
                        }
                        Log.d("isliked", String.valueOf(postFeed.getLiked())+postFeed.getPostId());
                    }

                    @Override
                    public void onFailure(DatabaseError databaseError) {
                        // Handle failure here
                    }
                });


                holder.likeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.d("postid", String.valueOf(postFeed.getLiked()));
                        if (postFeed.getLiked()) {
                            holder.likesCount.setText(postFeed.decreaseLike());

                            holder.likeIcon.setImageResource(R.drawable.baseline_favorite_border_24);
                            readData("likes/"+userId+"/"+postId, new RecyclerViewAdapter.FirebaseCallback() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    DatabaseReference childReference = dataSnapshot.getRef().child(mAuth.getUid());


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
                            Log.d("liked",mAuth.getUid()+" "+postId);
                            String postUserId=mAuth.getUid();

                            DatabaseReference likeRefernce= FirebaseDatabase.getInstance().getReference("likes");
                            likeRefernce.child(userId).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.hasChild(mAuth.getUid()))
                                    {
                                        Map<String, Object> postDetails = new HashMap<>();
                                        postDetails.put(mAuth.getUid(),"liked");


                                        if (postId != null) {
                                            likeRefernce.child(userId).child(postId).updateChildren(postDetails)
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
                holder.imageView.setOnClickListener(new PostDataAdapter.DoubleClickListener() {
                    @Override
                    public void onDoubleClick(View v) {
                        // holder.insideImage.setVisibility(View.VISIBLE);
                        holder.likeIcon.setImageResource(R.drawable.baseline_favorite_24);
                        holder.likeIcon.startAnimation(zoomInAnim);
                        holder.insideImage.startAnimation(zoomInAnim);
                        holder.insideImage.startAnimation(zoomOutAnim);

                        Log.d("liked",mAuth.getUid()+" "+postId);
                        String postUserId=userId;

                        DatabaseReference likeRefernce= FirebaseDatabase.getInstance().getReference("likes");
                        likeRefernce.child(postUserId).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.hasChild(mAuth.getUid()))
                                {
                                    Map<String, Object> postDetails = new HashMap<>();
                                    postDetails.put(mAuth.getUid(),"liked");
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

            }



            // display USERS Posts
            Picasso.get().load(imageUris.get(position)).into(holder.imageView);

            if(mAuth.getUid().equals(userId))
            {
                holder.more.setVisibility(View.VISIBLE);
                holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Declare outerBuilder as final
                        final AlertDialog.Builder outerBuilder = new AlertDialog.Builder(context);
                        outerBuilder.setTitle("More:");

                        // Declare outerDialog as final
                        final AlertDialog[] outerDialog = new AlertDialog[1];

                        SpannableString spannableString = new SpannableString("\nDelete Post");
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                AlertDialog.Builder innerBuilder = new AlertDialog.Builder(context);
                                innerBuilder.setTitle("Are you sure?");
                                TextView message = new TextView(context);
                                message.setText("You want to Delete Posts");
                                message.setTextColor(Color.RED);
                                message.setTextSize(18);
                                message.setTypeface(null, Typeface.BOLD);
                                message.setGravity(Gravity.CENTER);
                                message.setPadding(0, 32, 0, 0);
                                innerBuilder.setView(message);
                                innerBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        DatabaseReference postsRef = databaseReference.child("posts").child(userId);
                                        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists())
                                                {
                                                    for(DataSnapshot postIDs : dataSnapshot.getChildren())
                                                    {
                                                        String postPath = postIDs.child("imageURL").getValue(String.class);
                                                        if(imageUris.get(position).equals(postPath))
                                                        {
                                                            DatabaseReference deletePost = databaseReference.child("posts").child(userId).child(postIDs.getKey());
                                                            deletePost.removeValue();

                                                            deletePostFromFirebaseStorage(postPath);

                                                            Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT).show();
                                                            Intent i = new Intent(context,MainActivity.class);
                                                            i.putExtra("moveToMyProfile","moveToMyProfile");
                                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            context.startActivity(i);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.e("PostDataAdapter-311Line", "Error creating inner dialog: ");
                                            }
                                        });

                                        // Dismiss the outer dialog
                                        if (outerDialog[0] != null && outerDialog[0].isShowing()) {
                                            outerDialog[0].dismiss();
                                        }
                                    }
                                });
                                innerBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        // Dismiss the outer dialog
                                        if (outerDialog[0] != null && outerDialog[0].isShowing()) {
                                            outerDialog[0].dismiss();
                                        }
                                    }
                                });

                                AlertDialog innerDialog = innerBuilder.create();
                                innerDialog.show();
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                // Customize the appearance of the clickable text if needed
                                ds.setUnderlineText(false); // Remove underline
                                ds.setColor(Color.rgb(65, 105, 225)); // Change text color
                            }
                        };
                        spannableString.setSpan(clickableSpan, 1, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                        SpannableString spannableStringCopyUrl = new SpannableString("\n\nCopy Image Url");
                        ClickableSpan clickableSpanCopyUrl = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {

                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Image URL", imageUris.get(position));
                                if (clipboard != null) {
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(context, "Image URL copied to clipboard", Toast.LENGTH_SHORT).show();
                                }


                                if (outerDialog[0] != null && outerDialog[0].isShowing()) {
                                    outerDialog[0].dismiss();
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                // Customize the appearance of the clickable text if needed
                                ds.setUnderlineText(false); // Remove underline
                                ds.setColor(Color.rgb(65, 105, 225)); // Change text color
                            }
                        };
                        spannableStringCopyUrl.setSpan(clickableSpanCopyUrl, 1, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        SpannableString spannableStringDownload = new SpannableString("\n\nDownload Post");
                        ClickableSpan clickableSpanDownload = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {

                                downloadPost(imageUris.get(position));

                                if (outerDialog[0] != null && outerDialog[0].isShowing()) {
                                    outerDialog[0].dismiss();
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                // Customize the appearance of the clickable text if needed
                                ds.setUnderlineText(false); // Remove underline
                                ds.setColor(Color.rgb(65, 105, 225)); // Change text color
                            }
                        };
                        spannableStringDownload.setSpan(clickableSpanDownload, 1, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                        SpannableStringBuilder combinedSpannableString = new SpannableStringBuilder();
                        combinedSpannableString.append(spannableString);
                        combinedSpannableString.append(spannableStringCopyUrl);
                        combinedSpannableString.append(spannableStringDownload);



                        TextView message = new TextView(context);
                        message.setText(combinedSpannableString);
                        message.setTextColor(Color.BLACK);
                        message.setTextSize(16);
                        message.setTypeface(null, Typeface.BOLD);
                        message.setGravity(Gravity.LEFT);
                        message.setPadding(32, 0, 0, 32);
                        message.setMovementMethod(LinkMovementMethod.getInstance());
                        outerBuilder.setView(message);

                        // Create and show the outer dialog
                        outerDialog[0] = outerBuilder.create();
                        outerDialog[0].show();
                    }
                });

            }

            else{


                holder.more.setVisibility(View.VISIBLE);

                holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Declare outerBuilder as final
                        final AlertDialog.Builder outerBuilder = new AlertDialog.Builder(context);
                        outerBuilder.setTitle("More:");

                        // Declare outerDialog as final
                        final AlertDialog[] outerDialog = new AlertDialog[1];

                        SpannableString spannableStringCopyUrl = new SpannableString("\nCopy Image Url");
                        ClickableSpan clickableSpanCopyUrl = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {

                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Image URL", imageUris.get(position));
                                if (clipboard != null) {
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(context, "Image URL copied to clipboard", Toast.LENGTH_SHORT).show();
                                }


                                if (outerDialog[0] != null && outerDialog[0].isShowing()) {
                                    outerDialog[0].dismiss();
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                // Customize the appearance of the clickable text if needed
                                ds.setUnderlineText(false); // Remove underline
                                ds.setColor(Color.rgb(65, 105, 225)); // Change text color
                            }
                        };
                        spannableStringCopyUrl.setSpan(clickableSpanCopyUrl, 1, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        SpannableString spannableStringDownload = new SpannableString("\n\nDownload Post");
                        ClickableSpan clickableSpanDownload = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {

                                downloadPost(imageUris.get(position));

                                if (outerDialog[0] != null && outerDialog[0].isShowing()) {
                                    outerDialog[0].dismiss();
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                // Customize the appearance of the clickable text if needed
                                ds.setUnderlineText(false); // Remove underline
                                ds.setColor(Color.rgb(65, 105, 225)); // Change text color
                            }
                        };
                        spannableStringDownload.setSpan(clickableSpanDownload, 1, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                        SpannableStringBuilder combinedSpannableString = new SpannableStringBuilder();
                        combinedSpannableString.append(spannableStringCopyUrl);
                        combinedSpannableString.append(spannableStringDownload);



                        TextView message = new TextView(context);
                        message.setText(combinedSpannableString);
                        message.setTextColor(Color.BLACK);
                        message.setTextSize(16);
                        message.setTypeface(null, Typeface.BOLD);
                        message.setGravity(Gravity.LEFT);
                        message.setPadding(32, 0, 0, 32);
                        message.setMovementMethod(LinkMovementMethod.getInstance());
                        outerBuilder.setView(message);

                        // Create and show the outer dialog
                        outerDialog[0] = outerBuilder.create();
                        outerDialog[0].show();


                    }
                });

            }

            DatabaseReference usersRef = databaseReference.child("Users");
            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        username = dataSnapshot.child("name").getValue(String.class);
                        holder.username.setText(username);
                        String imagePath = dataSnapshot.child("profileImage").getValue(String.class);
                        Picasso.get().load(imagePath).into(holder.userProfieImage);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("PostDataAdapter-379Line", "Error in Binding username,profile: ");
                }
            });

            DatabaseReference postCaptionRef = databaseReference.child("posts").child(userId);
            postCaptionRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot getCaption : dataSnapshot.getChildren())
                    {
                        String imagePath = getCaption.child("imageURL").getValue(String.class);
                        if(imagePath.equals(imageUris.get(position)))
                        {
                            String caption = getCaption.child("caption").getValue(String.class);
                            if(caption.isEmpty() || caption.trim().isEmpty())
                                holder.postCaption.setText("");
                            else
                            {
                                DatabaseReference usersRef = databaseReference.child("Users");
                                usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String usernameText = dataSnapshot.child("name").getValue(String.class);

                                            if (usernameText != null && !usernameText.isEmpty()) {
                                                SpannableString spannableString = new SpannableString(usernameText + "  " + caption);
                                                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, usernameText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                holder.postCaption.setText(spannableString);
                                            } else {
                                                // Handle the case when username is null or empty
                                                holder.postCaption.setText(caption);
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("PostDataAdapter-379Line", "Error in Binding username,profile: ");
                                    }
                                });

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("PostDataAdapter-408Line", "Error in Binding post caption: ");
                }
            });
        }

        else
        {
            // for GLOBAL POSTS

            if(position == 0)
            {
                holder.commentIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, CommentActivity.class);
                        intent.putExtra("postId",firstPostId);
                        intent.putExtra("postUserId",firstImageUserId);

                        // Start the new activity
                        context.startActivity(intent);

                    }
                });
                // use globalPostIds.get(0) id for like when user likes first image only
                Log.d("PostIdOfFirstImage",firstPostId);
                // post url of first image
                Log.d("PostIdOfFirstImageUrl",firstImage);
                // user id of 1st post so use if for likes
                Log.d("UserIdFirstPost",firstImageUserId);

                PostFeed postFeed=new PostFeed();
                String postId=firstPostId;

                readData("likes/"+firstImageUserId+"/"+postId, new RecyclerViewAdapter.FirebaseCallback() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        // Handle successful data retrieval here
                        postFeed.setLikesCount((int) dataSnapshot.getChildrenCount());

                        holder.likesCount.setText(postFeed.getLikesCount());
                        if(dataSnapshot.hasChild(mAuth.getUid()))
                        {
                            holder.likeIcon.setImageResource(R.drawable.baseline_favorite_24);
                            postFeed.setLiked(true);
                            Log.d("milan","suc");
                        }
                        else{
                            postFeed.setLiked(false);
                            Log.d("milan","suc1");
                        }
                        Log.d("isliked", String.valueOf(postFeed.getLiked())+postFeed.getPostId());
                    }

                    @Override
                    public void onFailure(DatabaseError databaseError) {
                        // Handle failure here
                    }
                });


                holder.likeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.d("postid", String.valueOf(postFeed.getLiked()));
                        if (postFeed.getLiked()) {
                            holder.likesCount.setText(postFeed.decreaseLike());

                            holder.likeIcon.setImageResource(R.drawable.baseline_favorite_border_24);
                            readData("likes/"+firstImageUserId+"/"+postId, new RecyclerViewAdapter.FirebaseCallback() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    DatabaseReference childReference = dataSnapshot.getRef().child(mAuth.getUid());


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
                            Log.d("liked",mAuth.getUid()+" "+postId);
                            String postUserId=mAuth.getUid();

                            DatabaseReference likeRefernce= FirebaseDatabase.getInstance().getReference("likes");
                            likeRefernce.child(firstImageUserId).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.hasChild(mAuth.getUid()))
                                    {
                                        Map<String, Object> postDetails = new HashMap<>();
                                        postDetails.put(mAuth.getUid(),"liked");


                                        if (postId != null) {
                                            likeRefernce.child(firstImageUserId).child(postId).updateChildren(postDetails)
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
                holder.imageView.setOnClickListener(new PostDataAdapter.DoubleClickListener() {
                    @Override
                    public void onDoubleClick(View v) {
                        // holder.insideImage.setVisibility(View.VISIBLE);
                        holder.likeIcon.setImageResource(R.drawable.baseline_favorite_24);
                        holder.likeIcon.startAnimation(zoomInAnim);
                        holder.insideImage.startAnimation(zoomInAnim);
                        holder.insideImage.startAnimation(zoomOutAnim);

                        Log.d("liked",mAuth.getUid()+" "+postId);
                        String postUserId=firstImageUserId;

                        DatabaseReference likeRefernce= FirebaseDatabase.getInstance().getReference("likes");
                        likeRefernce.child(postUserId).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.hasChild(mAuth.getUid()))
                                {
                                    Map<String, Object> postDetails = new HashMap<>();
                                    postDetails.put(mAuth.getUid(),"liked");
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

                // loading the post which is clicked
                Picasso.get().load(firstImage).into(holder.imageView);

                holder.more.setVisibility(View.VISIBLE);
                holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Declare outerBuilder as final
                        final AlertDialog.Builder outerBuilder = new AlertDialog.Builder(context);
                        outerBuilder.setTitle("More:");

                        // Declare outerDialog as final
                        final AlertDialog[] outerDialog = new AlertDialog[1];

                        SpannableString spannableStringCopyUrl = new SpannableString("\nCopy Image Url");
                        ClickableSpan clickableSpanCopyUrl = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {

                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Image URL", firstImage);
                                if (clipboard != null) {
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(context, "Image URL copied to clipboard", Toast.LENGTH_SHORT).show();
                                }


                                if (outerDialog[0] != null && outerDialog[0].isShowing()) {
                                    outerDialog[0].dismiss();
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                // Customize the appearance of the clickable text if needed
                                ds.setUnderlineText(false); // Remove underline
                                ds.setColor(Color.rgb(65, 105, 225)); // Change text color
                            }
                        };
                        spannableStringCopyUrl.setSpan(clickableSpanCopyUrl, 1, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        SpannableString spannableStringDownload = new SpannableString("\n\nDownload Post");
                        ClickableSpan clickableSpanDownload = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {

                                downloadPost(firstImage);

                                if (outerDialog[0] != null && outerDialog[0].isShowing()) {
                                    outerDialog[0].dismiss();
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                // Customize the appearance of the clickable text if needed
                                ds.setUnderlineText(false); // Remove underline
                                ds.setColor(Color.rgb(65, 105, 225)); // Change text color
                            }
                        };
                        spannableStringDownload.setSpan(clickableSpanDownload, 1, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                        SpannableStringBuilder combinedSpannableString = new SpannableStringBuilder();
                        combinedSpannableString.append(spannableStringCopyUrl);
                        combinedSpannableString.append(spannableStringDownload);



                        TextView message = new TextView(context);
                        message.setText(combinedSpannableString);
                        message.setTextColor(Color.BLACK);
                        message.setTextSize(16);
                        message.setTypeface(null, Typeface.BOLD);
                        message.setGravity(Gravity.LEFT);
                        message.setPadding(32, 0, 0, 32);
                        message.setMovementMethod(LinkMovementMethod.getInstance());
                        outerBuilder.setView(message);

                        // Create and show the outer dialog
                        outerDialog[0] = outerBuilder.create();
                        outerDialog[0].show();


                    }
                });



                // post id of first image
                // use globalPostIds.get(0) id for like when user likes first image only
                //Log.d("PostIdOfFirstImage",globalPostIds.get(0));
                // post url of first image
                //Log.d("PostIdOfFirstImageUrl",firstImage);
                // user id of 1st post so use if for likes
                //Log.d("UserIdFirstPost",firstImageUserId);

                //loading username and profile of one who posted
                DatabaseReference usersRef = databaseReference.child("Users");
                usersRef.child(firstImageUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String username = dataSnapshot.child("name").getValue(String.class);
                            holder.username.setText(username);
                            String imagePath = dataSnapshot.child("profileImage").getValue(String.class);
                            Picasso.get().load(imagePath).into(holder.userProfieImage);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(context,viewUserProfile.class);
                        i.putExtra("username",holder.username.getText().toString());

                        if(currentUserName.equals(holder.username.getText().toString()))
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

                //loading caption of post
                DatabaseReference postCaptionRef = databaseReference.child("posts").child(firstImageUserId);
                postCaptionRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot getCaption : dataSnapshot.getChildren())
                        {
                            String imagePath = getCaption.child("imageURL").getValue(String.class);
                            if(imagePath.equals(firstImage))
                            {
                                String caption = getCaption.child("caption").getValue(String.class);
                                if(caption.isEmpty() || caption.trim().isEmpty())
                                    holder.postCaption.setText("");
                                else
                                {
                                    DatabaseReference usersRef = databaseReference.child("Users");
                                    usersRef.child(firstImageUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                String username = dataSnapshot.child("name").getValue(String.class);

                                                if (username != null && !username.isEmpty()) {
                                                    SpannableString spannableString = new SpannableString(username + " " + caption);
                                                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    holder.postCaption.setText(spannableString);
                                                }
                                                else
                                                {
                                                    holder.postCaption.setText(caption);
                                                }

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
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

                int otherPostsPosition = position-1;
                //loading username and profile of one who posted
                DatabaseReference usersRef = databaseReference.child("Users");
                usersRef.child(userIDs.get(otherPostsPosition)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String username = dataSnapshot.child("name").getValue(String.class);
                            holder.username.setText(username);
                            String imagePath = dataSnapshot.child("profileImage").getValue(String.class);
                            Picasso.get().load(imagePath).into(holder.userProfieImage);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(context,viewUserProfile.class);
                        i.putExtra("username",holder.username.getText().toString());

                        if(currentUserName.equals(holder.username.getText().toString()))
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


                //loading caption of post
                DatabaseReference postCaptionRef = databaseReference.child("posts").child(userIDs.get(otherPostsPosition));
                postCaptionRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot getCaption : dataSnapshot.getChildren())
                        {
                            String imagePath = getCaption.child("imageURL").getValue(String.class);
                            if(imagePath.equals(allPostsURL.get(otherPostsPosition)))
                            {
                                String caption = getCaption.child("caption").getValue(String.class);
                                if(caption.isEmpty() || caption.trim().isEmpty())
                                    holder.postCaption.setText("");
                                else
                                {
                                    DatabaseReference usersRef = databaseReference.child("Users");
                                    usersRef.child(userIDs.get(otherPostsPosition)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                String username = dataSnapshot.child("name").getValue(String.class);

                                                if(username != null && !username.isEmpty())
                                                {
                                                    SpannableString spannableString = new SpannableString( holder.username.getText().toString() + " " + caption);
                                                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, holder.username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    holder.postCaption.setText(spannableString);
                                                }
                                                else
                                                {
                                                    holder.postCaption.setText(caption);
                                                }

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // rest of post url id so use this id when user likes i.e from 2nd post
                Log.d("ALLPOSTURLID",newGlobalPostIds.get(otherPostsPosition));
                // rest of post url i.e from 2nd post
                Log.d("ALLPOSTURL",allPostsURL.get(otherPostsPosition));
                // user id of rest of post so use it for likes
                Log.d("UserIdForRestPosts",userIDs.get(otherPostsPosition));
                // loading the rest of posts
                Picasso.get().load(allPostsURL.get(otherPostsPosition)).into(holder.imageView);

                holder.more.setVisibility(View.VISIBLE);
                holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Declare outerBuilder as final
                        final AlertDialog.Builder outerBuilder = new AlertDialog.Builder(context);
                        outerBuilder.setTitle("More:");

                        // Declare outerDialog as final
                        final AlertDialog[] outerDialog = new AlertDialog[1];

                        SpannableString spannableStringCopyUrl = new SpannableString("\nCopy Image Url");
                        ClickableSpan clickableSpanCopyUrl = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {

                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Image URL", allPostsURL.get(otherPostsPosition));
                                if (clipboard != null) {
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(context, "Image URL copied to clipboard", Toast.LENGTH_SHORT).show();
                                }


                                if (outerDialog[0] != null && outerDialog[0].isShowing()) {
                                    outerDialog[0].dismiss();
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                // Customize the appearance of the clickable text if needed
                                ds.setUnderlineText(false); // Remove underline
                                ds.setColor(Color.rgb(65, 105, 225)); // Change text color
                            }
                        };
                        spannableStringCopyUrl.setSpan(clickableSpanCopyUrl, 1, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        SpannableString spannableStringDownload = new SpannableString("\n\nDownload Post");
                        ClickableSpan clickableSpanDownload = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {

                                downloadPost(allPostsURL.get(otherPostsPosition));

                                if (outerDialog[0] != null && outerDialog[0].isShowing()) {
                                    outerDialog[0].dismiss();
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                // Customize the appearance of the clickable text if needed
                                ds.setUnderlineText(false); // Remove underline
                                ds.setColor(Color.rgb(65, 105, 225)); // Change text color
                            }
                        };
                        spannableStringDownload.setSpan(clickableSpanDownload, 1, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                        SpannableStringBuilder combinedSpannableString = new SpannableStringBuilder();
                        combinedSpannableString.append(spannableStringCopyUrl);
                        combinedSpannableString.append(spannableStringDownload);



                        TextView message = new TextView(context);
                        message.setText(combinedSpannableString);
                        message.setTextColor(Color.BLACK);
                        message.setTextSize(16);
                        message.setTypeface(null, Typeface.BOLD);
                        message.setGravity(Gravity.LEFT);
                        message.setPadding(32, 0, 0, 32);
                        message.setMovementMethod(LinkMovementMethod.getInstance());
                        outerBuilder.setView(message);

                        // Create and show the outer dialog
                        outerDialog[0] = outerBuilder.create();
                        outerDialog[0].show();



                    }
                });



                PostFeed postFeed=new PostFeed();
                String postId=newGlobalPostIds.get(position-1);
                String postuserid=userIDs.get(position-1);
           //     Log.d("firstpost",globalPostIds.get(0));
             //   Log.d("firstuser",firstImageUserId);

                DatabaseReference likeRef=FirebaseDatabase.getInstance().getReference("likes");
                likeRef.child(postuserid).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postFeed.setLikesCount((int) dataSnapshot.getChildrenCount());

                        holder.likesCount.setText(postFeed.getLikesCount());
                        if(dataSnapshot.hasChild(mAuth.getUid()))
                        {
                            holder.likeIcon.setImageResource(R.drawable.baseline_favorite_24);
                            postFeed.setLiked(true);
                            Log.d("milan","suc");
                        }
                        else{
                            postFeed.setLiked(false);
                            Log.d("milan","suc1");
                        }
                        Log.d("isliked", String.valueOf(postFeed.getLiked())+postFeed.getPostId());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.commentIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, CommentActivity.class);
                        intent.putExtra("postId",postId);
                        intent.putExtra("postUserId",postuserid);




                        // Start the new activity
                        context.startActivity(intent);

                    }
                });
                holder.likeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("postid", String.valueOf(postFeed.getLiked()));
                        if (postFeed.getLiked()) {
                            holder.likesCount.setText(postFeed.decreaseLike());

                            holder.likeIcon.setImageResource(R.drawable.baseline_favorite_border_24);
                            readData("likes/"+postuserid+"/"+postId, new RecyclerViewAdapter.FirebaseCallback() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    DatabaseReference childReference = dataSnapshot.getRef().child(mAuth.getUid());


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
                            Log.d("liked",mAuth.getUid()+" "+postId);
                            String postUserId=mAuth.getUid();

                            DatabaseReference likeRefernce= FirebaseDatabase.getInstance().getReference("likes");
                            likeRefernce.child(postuserid).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.hasChild(mAuth.getUid()))
                                    {
                                        Map<String, Object> postDetails = new HashMap<>();
                                        postDetails.put(mAuth.getUid(),"liked");


                                        if (postId != null) {
                                            likeRefernce.child(postuserid).child(postId).updateChildren(postDetails)
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

                holder.imageView.setOnClickListener(new PostDataAdapter.DoubleClickListener() {
                    @Override
                    public void onDoubleClick(View v) {
                        // holder.insideImage.setVisibility(View.VISIBLE);
                        holder.likeIcon.setImageResource(R.drawable.baseline_favorite_24);
                        holder.likeIcon.startAnimation(zoomInAnim);
                        holder.insideImage.startAnimation(zoomInAnim);
                        holder.insideImage.startAnimation(zoomOutAnim);

                        Log.d("liked",mAuth.getUid()+" "+postId);
                        String postUserId=postuserid;

                        DatabaseReference likeRefernce= FirebaseDatabase.getInstance().getReference("likes");
                        likeRefernce.child(postUserId).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.hasChild(mAuth.getUid()))
                                {
                                    Map<String, Object> postDetails = new HashMap<>();
                                    postDetails.put(mAuth.getUid(),"liked");
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



              /*  readData("likes/"+postuserid+"/"+postId, new RecyclerViewAdapter.FirebaseCallback() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        // Handle successful data retrieval here
                        postFeed.setLikesCount((int) dataSnapshot.getChildrenCount());

                        holder.likesCount.setText(postFeed.getLikesCount());
                        if(dataSnapshot.hasChild(mAuth.getUid()))
                        {
                            holder.likeIcon.setImageResource(R.drawable.baseline_favorite_24);
                            postFeed.setLiked(true);
                            Log.d("milan","suc");
                        }
                        else{
                            postFeed.setLiked(false);
                            Log.d("milan","suc1");
                        }
                        Log.d("isliked", String.valueOf(postFeed.getLiked())+postFeed.getPostId());
                    }

                    @Override
                    public void onFailure(DatabaseError databaseError) {
                        // Handle failure here
                    }
                });
*/


            }

        }
    }


    @Override
    public int getItemCount() {
        if (imageUris == null) {
            if (userIDs != null) {
                int count = userIDs.size() + 1;
                Log.d("PostDataAdapter", "getItemCount: " + count);
                return count;
            } else {
                Log.e("PostDataAdapter", "userIDs is null");
                return 0;  // or return a default value, depending on your logic
            }
        } else {
            int count = imageUris.size();
            Log.d("PostDataAdapter", "getItemCount: " + count);
            return count;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView,more;
        ImageView likeIcon,insideImage;
        CircleImageView userProfieImage;
        TextView username,postCaption,likesCount;
        ImageView commentIcon;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            commentIcon=itemView.findViewById(R.id.commentIcon);
            imageView  = itemView.findViewById(R.id.userPosts);
            userProfieImage = itemView.findViewById(R.id.userPhoto);
            username = itemView.findViewById(R.id.username);
            likeIcon=itemView.findViewById(R.id.likeIcon);
            postCaption = itemView.findViewById(R.id.postcaption);
            more = itemView.findViewById(R.id.more);
            likesCount=itemView.findViewById(R.id.likescount);
            insideImage=itemView.findViewById(R.id.centerImage);
        }

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
}
