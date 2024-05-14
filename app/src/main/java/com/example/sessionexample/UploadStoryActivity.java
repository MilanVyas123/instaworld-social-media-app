package com.example.sessionexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class UploadStoryActivity extends AppCompatActivity {

    private Uri imageUri;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_story);
        ImageView imageView = findViewById(R.id.storyImage);

        Intent intent = getIntent();
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        Toast.makeText(this, uid, Toast.LENGTH_SHORT).show();
        if (intent != null) {
            String imageUriString = intent.getStringExtra("IMAGE_URI");
            if (imageUriString != null) {
                try {
                    imageUri = Uri.parse(imageUriString);
                    imageView.setImageURI(imageUri);
                } catch (Exception e) {
                    Log.d("storyerror", e.toString()); // Log the exception
                }
            } else {
                // Handle the case when imageUriString is null
            }
        } else {
            // Handle the case when the intent is null
        }
    }

    public void uploadStory(View view)
    {


        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("story");
        String imageName = imageUri.getLastPathSegment();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String uniqueImageName = timeStamp + "_"+uid;

        StorageReference imageRef = storageRef.child(uniqueImageName);
        Toast.makeText(this, "Clicked"+uniqueImageName, Toast.LENGTH_SHORT).show();
        // Upload the image to Firebase Storage
        UploadTask uploadTask = imageRef.putFile(imageUri);


        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("story").child(uid);
                // String postId = databaseRef.push().getKey();
                String userId=uid;
                Map<String, Object> postDetails = new HashMap<>();
                postDetails.put("storyURL", imageUrl);
                postDetails.put("storyUploadTimestamp",timeStamp);

                String postId=databaseRef.push().getKey();
                if (postId != null) {
                    databaseRef.child(postId).setValue(postDetails)
                            .addOnSuccessListener(aVoid -> {
                                // Data successfully added to the database
                                // You can display a success message or take further actions here
                                // progressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(this, HomeFragment.class);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                // Handle any errors that occurred while adding data to the database
                                // progressBar.setVisibility(View.GONE);
                            });
                }
            });
        }).addOnFailureListener(e -> {
            // Handle any errors that may occur during the upload process
            //progressBar.setVisibility(View.GONE);
        });
    }
}



