package com.example.sessionexample.ReelsPackage;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sessionexample.MainActivity;
import com.example.sessionexample.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class Upload_Reel extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    Uri videoUri;
    VideoView videoPreview;
    EditText captionEditText;
    String caption;
    Button uploadReelButton;
    private StorageReference storageReference;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_reel);

        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference().child("reels").child(mAuth.getUid());
        storageReference = FirebaseStorage.getInstance().getReference();

        videoPreview = findViewById(R.id.videoPreview);
        videoUri = getIntent().getData();
        videoPreview.setVideoURI(videoUri);
        videoPreview.start();

        videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });

        captionEditText = findViewById(R.id.captionEditText);

        progressBar = findViewById(R.id.progressBar);

        uploadReelButton = findViewById(R.id.uploadReelButton);
        uploadReelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                caption = captionEditText.getText().toString();

                progressBar.setVisibility(View.VISIBLE);

                uploadVideoURL(videoUri, caption);
            }
        });

    }

    private void uploadVideoURL(Uri videoUri, final String caption) {

        StorageReference userReelsRef = storageReference.child("reels").child(mAuth.getUid());

        String videoFileName = "video_" + System.currentTimeMillis() + ".mp4";

        StorageReference videoRef = userReelsRef.child(videoFileName);

        UploadTask uploadTask = videoRef.putFile(videoUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return videoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // Upload succeeded, get the download URL
                    Uri downloadUri = task.getResult();

                    // Call a method to store the download URL and caption in Firebase Realtime Database
                    storeDataInDatabase(downloadUri.toString(), caption);
                } else {
                    // Handle failures
                    Log.e("Upload", "Failed to upload video", task.getException());
                }
            }
        });

    }

    private void storeDataInDatabase(String videoURL, String caption) {
        // Generate a unique reelId (You can use push() to generate a unique key)
        String reelId = databaseReference.push().getKey();

        // Create a child reference for the reelId
        DatabaseReference reelRef = databaseReference.child(reelId);

        // Create a HashMap to hold the data
        HashMap<String, Object> reelData = new HashMap<>();
        reelData.put("videoURL", videoURL);
        reelData.put("caption", caption);

        // Set the data to the reelId location in Firebase Realtime Database
        reelRef.setValue(reelData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Data stored successfully

                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(Upload_Reel.this,"Reel Uploaded",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(Upload_Reel.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);

                            Log.d("Upload", "Data stored successfully");
                        } else {
                            // Handle failures
                            Log.e("Upload", "Failed to store data", task.getException());
                        }
                    }
                });
    }
}