package com.example.sessionexample;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class Uploads extends AppCompatActivity {

    private ImageView imagePreview;
    private EditText captionEditText;
    ProgressBar progressBar;
    private Button chooseImageButton;
    private Button uploadImageButton;
    private Uri imageUri; // Store the selected image URI
    private String uid;
    private ActivityResultLauncher<Intent> imageActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploads);
        imagePreview =findViewById(R.id.imagePreview);
        captionEditText = findViewById(R.id.captionEditText);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        progressBar = findViewById(R.id.progressBar);
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("uid",uid);

        chooseImageButton.setOnClickListener(v -> openFileChooser());

        uploadImageButton.setOnClickListener(v -> uploadImage());

        // Initialize Activity Result Launcher for image selection
        imageActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == this.RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                imagePreview.setImageURI(imageUri);
            }
        });

    }

    private void uploadImage() {
        if (imageUri != null) {
            // Show ProgressBar to indicate progress

            progressBar.setVisibility(View.VISIBLE);

            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("post");
            String imageName = imageUri.getLastPathSegment();
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String uniqueImageName = timeStamp + "_" + imageUri.getLastPathSegment();

            StorageReference imageRef = storageRef.child(uniqueImageName);

            // Upload the image to Firebase Storage
            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
            });

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("posts").child(uid);
                    // String postId = databaseRef.push().getKey();
                    String userId = uid;
                    Map<String, Object> postDetails = new HashMap<>();
                    postDetails.put("imageURL", imageUrl);
                    postDetails.put("caption", String.valueOf(captionEditText.getText()));
                    String postId = databaseRef.push().getKey();
                    if (postId != null) {
                        databaseRef.child(postId).setValue(postDetails)
                                .addOnSuccessListener(aVoid -> {
                                    // Data successfully added to the database
                                    // You can display a success message or take further actions here
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(this, "Post has been uploaded.", Toast.LENGTH_SHORT).show();
                                    startHomeFragment();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle any errors that occurred while adding data to the database
                                    progressBar.setVisibility(View.GONE);
                                });
                    }
                });
            }).addOnFailureListener(e -> {
                // Handle any errors that may occur during the upload process
                progressBar.setVisibility(View.GONE);
            });
        }
    }

    private void startHomeFragment() {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imageActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));

    }
}