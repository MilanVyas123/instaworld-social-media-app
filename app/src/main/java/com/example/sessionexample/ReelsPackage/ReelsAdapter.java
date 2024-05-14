package com.example.sessionexample.ReelsPackage;


import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sessionexample.MainActivity;
import com.example.sessionexample.PostFeed;
import com.example.sessionexample.R;
import com.example.sessionexample.adapter.RecyclerViewAdapter;
import com.example.sessionexample.comment.CommentActivity;
import com.example.sessionexample.reels.ReelAdapter;
import com.example.sessionexample.viewUserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReelsAdapter extends RecyclerView.Adapter<ReelsAdapter.VideoViewHolder> {

    List<ReelsModel> videoList;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
    DatabaseReference databaseReference = database.getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Context context;
    File directory;

    public ReelsAdapter(Context context,List<ReelsModel> videoList) {
        this.context=context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_reel_item,parent,false);

        return new VideoViewHolder(view);
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
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {

        ReelsModel reelsModel=videoList.get(position);
        holder.setVideoData(videoList.get(position));

        String userIdd=reelsModel.getVideoUrlUserId();
        String reelIdd=reelsModel.getVideoUrlId();

        Animation zoomInAnim = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
        Animation zoomOutAnim = AnimationUtils.loadAnimation(context, R.anim.zoom_out);


        //Toast.makeText(context, reelsModel.getVideoUrlId(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, reelsModel.getVideoUrlUserId(), Toast.LENGTH_SHORT).show();

        DatabaseReference commentCountRef=FirebaseDatabase.getInstance().getReference("commentsForReels");

        commentCountRef.child(userIdd).child(reelIdd).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String comments= String.valueOf(dataSnapshot.getChildrenCount());
                //Toast.makeText(context, "Comments : "+comments, Toast.LENGTH_SHORT).show();
                holder.commentText.setText(comments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        readData("likesReels/"+userIdd+"/"+reelIdd, new RecyclerViewAdapter.FirebaseCallback() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                // Handle successful data retrieval here
                reelsModel.setLikesCount((int) dataSnapshot.getChildrenCount());

                holder.likesCount.setText(String.valueOf(reelsModel.getLikesCount()));
                if(dataSnapshot.hasChild(mAuth.getUid()))
                {
                    holder.likeIcon.setImageResource(R.drawable.baseline_favorite_24);
                    reelsModel.setLiked(true);
                }
                else{
                    reelsModel.setLiked(false);
                }
                Log.d("isliked", String.valueOf(reelsModel.getLiked())+reelsModel.getVideoUrlId());
            }



            @Override
            public void onFailure(DatabaseError databaseError) {
                // Handle failure here
            }
        });
        GestureDetector gestureDetector;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                // Handle double-tap event
                // For example, you can implement like or favorite action here

                //Toast.makeText(context, "Double Tap", Toast.LENGTH_SHORT).show();
                holder.likeIcon.setImageResource(R.drawable.baseline_favorite_24);
                holder.likeIcon.startAnimation(zoomInAnim);
                holder.insideImage.startAnimation(zoomInAnim);
                holder.insideImage.startAnimation(zoomOutAnim);

                Log.d("liked",userIdd+" "+reelIdd);
                String postUserId=userIdd;
                String postId=reelIdd;
                DatabaseReference likeRefernce= FirebaseDatabase.getInstance().getReference("likesReels");
                likeRefernce.child(postUserId).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChild(mAuth.getUid()))
                        {
                            Map<String, Object> postDetails = new HashMap<>();
                            postDetails.put(mAuth.getUid(), "liked");
                            holder.likesCount.setText(reelsModel.increaseLike());


                            if (postId != null) {
                                likeRefernce.child(postUserId).child(postId).updateChildren(postDetails)
                                        .addOnSuccessListener(aVoid -> {
                                            reelsModel.setLiked(!reelsModel.getLiked());
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
                return true;
            }
        });

        holder.videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });


        holder.likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("postid", String.valueOf(reelsModel.getLiked()));
                if (reelsModel.getLiked()) {
                    holder.likesCount.setText(reelsModel.decreaseLike());

                    holder.likeIcon.setImageResource(R.drawable.baseline_favorite_border_24);
                    readData("likesReels/"+userIdd+"/"+reelIdd, new RecyclerViewAdapter.FirebaseCallback() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            DatabaseReference childReference = dataSnapshot.getRef().child(mAuth.getUid());


                            // Remove the data
                            childReference.removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        reelsModel.setLiked(!reelsModel.getLiked());
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
                    holder.likesCount.setText(reelsModel.increaseLike());
                    holder.likeIcon.setImageResource(R.drawable.baseline_favorite_24);
                    holder.insideImage.startAnimation(zoomInAnim);
                    holder.insideImage.startAnimation(zoomOutAnim);
                    Log.d("liked",userIdd+" "+reelIdd);
                    String postUserId=reelsModel.getVideoUrlUserId();
                    String postId=reelsModel.getVideoUrlId();
                    DatabaseReference likeRefernce= FirebaseDatabase.getInstance().getReference("likesReels");
                    likeRefernce.child(postUserId).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.hasChild(mAuth.getUid()))
                            {
                                Map<String, Object> postDetails = new HashMap<>();
                                postDetails.put(mAuth.getUid(), "liked");


                                if (postId != null) {
                                    likeRefernce.child(postUserId).child(postId).updateChildren(postDetails)
                                            .addOnSuccessListener(aVoid -> {
                                                reelsModel.setLiked(!reelsModel.getLiked());
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
                String commentPostId=reelIdd;
                String postUserId=userIdd;
                // Intent intent = new Intent(getContext(), ViewStory.class);
                // intent.putExtra("userkey", userKey);

                //startActivity(intent);
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId",commentPostId);
                intent.putExtra("postUserId",postUserId);
                intent.putExtra("forReels","true");




                // Start the new activity
                context.startActivity(intent);


            }
        });



    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder{

        VideoView videoView;
        TextView title, desc, likeText, commentText,likesCount;
        ImageView likeIcon,commentIcon,insideImage,more;
        CircleImageView userProfile;
        ProgressBar progressBar2;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            title = itemView.findViewById(R.id.video_title);
            desc = itemView.findViewById(R.id.video_desc);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            commentIcon = itemView.findViewById(R.id.commentIcon);
            userProfile = itemView.findViewById(R.id.userPhoto);
            likesCount = itemView.findViewById(R.id.likeText);
            commentText = itemView.findViewById(R.id.commentText);
            insideImage=itemView.findViewById(R.id.centerImage);
            progressBar2 = itemView.findViewById(R.id.progressBar2);
            more = itemView.findViewById(R.id.more);


        }

        private void deletePostFromFirebaseStorage(String reelPath)
        {

            String bucket = extractBucketFromUrl(reelPath);
            String path = extractPathFromUrl(reelPath);

            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://" + bucket + "/" + path);
            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    Toast.makeText(context,"Reel Deleted" , Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra("moveToMyProfile","moveToMyProfile");
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context,"Reel not Deleted" , Toast.LENGTH_SHORT).show();
                }
            });
        }

        private String extractBucketFromUrl(String reelPath) {
            // Extract the bucket name from the URL
            int start = reelPath.indexOf("/b/") + 3;
            int end = reelPath.indexOf("/o/");
            return reelPath.substring(start, end);
        }


        private String extractPathFromUrl(String reelPath) {
            // Extract the path from the URL
            int start = reelPath.indexOf("/o/") + 3;
            int end = reelPath.indexOf("?alt=media");
            return reelPath.substring(start, end).replace("%2F", "/");
        }

        private void downloadReel(String videoUrl) {
            // Create a reference to the video file
            StorageReference storageRef = storage.getReferenceFromUrl(videoUrl);

            // Get the download URL
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Start downloading the video using DownloadManager
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(uri);

                    // Specify the directory path where you want to save the video
                    directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "instaWorld-Reels");

                    // Create the directory if it doesn't exist
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    // Set the destination directory for the download request
                    request.setDestinationUri(Uri.fromFile(new File(directory, videoUrl + ".mp4")));
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    downloadManager.enqueue(request);

                    // Display a toast message
                    Toast.makeText(context, "Reel downloading...", Toast.LENGTH_SHORT).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        // Construct the file path to the downloaded video
                        String filePath = new File(directory, videoUrl + ".mp4").getPath();

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


                        Toast.makeText(context, "Reel downloaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to download reel", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void setVideoData(ReelsModel reelsModel){

            title.setText(reelsModel.getTitle());
            desc.setText(reelsModel.getDesc());
            videoView.setVideoPath(reelsModel.getVideoUrl());
            Picasso.get().load(reelsModel.getUserPhotoUrl()).into(userProfile);


            // here do likes and comment.. i have set likeIcon and commentIcon listener below.. watch out

            Log.d("videoUrl : " , reelsModel.getVideoUrl());
            Log.d("videoUrlId : " , reelsModel.getVideoUrlId());
            Log.d("videoUrlUserId : " , reelsModel.getVideoUrlUserId());


            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    progressBar2.setVisibility(View.GONE);

                    /*int[] location = new int[2];
                    likeIcon.getLocationOnScreen(location);

                    // Extract x and y coordinates
                    int x = location[0];
                    int y = location[1];

                    // Get the display metrics from the resources
                    DisplayMetrics displayMetrics = itemView.getContext().getResources().getDisplayMetrics();

                    // Calculate the screen height
                    int screenHeight = displayMetrics.heightPixels;

                    // Set the likeIcon's position to the bottom of the screen
                    int iconHeight = likeIcon.getHeight(); // Assuming the likeIcon has been measured and has a height
                    int bottomMargin = 850; // Adjust this value as needed
                    int newY = screenHeight - iconHeight - bottomMargin;

                    // Set the new position for the likeIcon
                    likeIcon.setY(newY);
                    likesCount.setY(newY+6);
                    commentIcon.setY(newY+8);
                    commentText.setY(newY+10); */

                    likeIcon.setVisibility(View.VISIBLE);
                    likesCount.setVisibility(View.VISIBLE);
                    commentText.setVisibility(View.VISIBLE);
                    commentIcon.setVisibility(View.VISIBLE);

                    if(reelsModel.getVideoUrlUserId().equals(mAuth.getUid())){

                        more.setVisibility(View.VISIBLE);

                        more.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // Declare outerBuilder as final
                                final AlertDialog.Builder outerBuilder = new AlertDialog.Builder(context);
                                outerBuilder.setTitle("More:");

                                // Declare outerDialog as final
                                final AlertDialog[] outerDialog = new AlertDialog[1];

                                SpannableString spannableString = new SpannableString("\nDelete Reel");
                                ClickableSpan clickableSpan = new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {
                                        AlertDialog.Builder innerBuilder = new AlertDialog.Builder(context);
                                        innerBuilder.setTitle("Are you sure?");
                                        TextView message = new TextView(context);
                                        message.setText("You want to Delete Reel");
                                        message.setTextColor(Color.RED);
                                        message.setTextSize(18);
                                        message.setTypeface(null, Typeface.BOLD);
                                        message.setGravity(Gravity.CENTER);
                                        message.setPadding(0, 32, 0, 0);
                                        innerBuilder.setView(message);
                                        innerBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                DatabaseReference reelsRef = databaseReference.child("reels").child(mAuth.getUid()).child(reelsModel.getVideoUrlId());
                                                reelsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        DatabaseReference likesReelRef = databaseReference.child("likesReels").child(mAuth.getUid()).child(reelsModel.getVideoUrlId());
                                                        likesReelRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                deletePostFromFirebaseStorage(reelsModel.getVideoUrl());


                                                            }
                                                        });


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

                                SpannableString spannableStringCopyUrl = new SpannableString("\n\nCopy Reel Url");
                                ClickableSpan clickableSpanCopyUrl = new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {

                                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("Reel URL", reelsModel.getVideoUrl());
                                        if (clipboard != null) {
                                            clipboard.setPrimaryClip(clip);
                                            Toast.makeText(context, "Reel URL copied to clipboard", Toast.LENGTH_SHORT).show();
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

                                SpannableString spannableStringDownload = new SpannableString("\n\nDownload Reel");
                                ClickableSpan clickableSpanDownload = new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {

                                        downloadReel(reelsModel.getVideoUrl());

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

                    else {

                        more.setVisibility(View.VISIBLE);

                        more.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                // Declare outerBuilder as final
                                final AlertDialog.Builder outerBuilder = new AlertDialog.Builder(context);
                                outerBuilder.setTitle("More:");

                                // Declare outerDialog as final
                                final AlertDialog[] outerDialog = new AlertDialog[1];

                                SpannableString spannableStringCopyUrl = new SpannableString("\nCopy Reel Url");
                                ClickableSpan clickableSpanCopyUrl = new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {

                                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("Reel URL", reelsModel.getVideoUrl());
                                        if (clipboard != null) {
                                            clipboard.setPrimaryClip(clip);
                                            Toast.makeText(context, "Reel URL copied to clipboard", Toast.LENGTH_SHORT).show();
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
                                spannableStringCopyUrl.setSpan(clickableSpanCopyUrl, 1, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                                SpannableString spannableStringDownload = new SpannableString("\n\nDownload Reel");
                                ClickableSpan clickableSpanDownload = new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {

                                        downloadReel(reelsModel.getVideoUrl());

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


                    // Log the coordinates
                    //Log.d("LikeIconPosition", "x: " + String.valueOf(x) + ", y: " + String.valueOf(y));


                    mp.start();


                    /*float videoWidth = mp.getVideoWidth();
                    float videoHeight = mp.getVideoHeight();
                    float videoAspectRatio = videoWidth / videoHeight;

                    float viewWidth = videoView.getWidth();
                    float viewHeight = videoView.getHeight();
                    float viewAspectRatio = viewWidth / viewHeight;

                    float scaleX = 1f;
                    float scaleY = 1f;

                    if (videoAspectRatio > viewAspectRatio) {
                        // Video is wider than the view, scale according to width
                        scaleX = viewWidth / videoWidth;
                    } else {
                        // Video is taller than or equal to the view, scale according to height
                        scaleY = viewHeight / videoHeight;
                    }

                    // Apply scaling
                    videoView.setScaleX(scaleX);
                    videoView.setScaleY(scaleY); */


                    //applyVideoViewScaling(mp);

                }
            });



            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    mp.start();

                }
            });


            likeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //likeIcon.setImageResource(R.drawable.baseline_favorite_24);

                    //Toast.makeText(v.getContext(), reelsModel.getVideoUrlId(),Toast.LENGTH_SHORT).show();
                    //Toast.makeText(v.getContext(), reelsModel.getVideoUrlUserId(),Toast.LENGTH_SHORT).show();

                }
            });


            commentIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Toast.makeText(v.getContext(), reelsModel.getVideoUrlId(),Toast.LENGTH_SHORT).show();
                    //Toast.makeText(v.getContext(), reelsModel.getVideoUrlUserId(),Toast.LENGTH_SHORT).show();

                }
            });

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(itemView.getContext(), viewUserProfile.class);
                    i.putExtra("username",reelsModel.getTitle());

                    if(reelsModel.getCurrentUsername().equals(reelsModel.getTitle()))
                    {
                        i.putExtra("currentUserName",reelsModel.getCurrentUsername());
                    }
                    else
                    {
                        i.putExtra("currentUserName","");
                    }

                    itemView.getContext().startActivity(i);

                }
            });

            userProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                        Intent i = new Intent(itemView.getContext(), viewUserProfile.class);
                        i.putExtra("username",reelsModel.getTitle());

                        if(reelsModel.getCurrentUsername().equals(reelsModel.getTitle()))
                        {
                            i.putExtra("currentUserName",reelsModel.getCurrentUsername());
                        }
                        else
                        {
                            i.putExtra("currentUserName","");
                        }

                        itemView.getContext().startActivity(i);


                }
            });

        }


    }

}
