package com.example.sessionexample;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.sessionexample.ReelsPackage.Upload_Reel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AddPost extends Fragment {
    private ImageView addPost,addReel,addStory;
    private EditText captionEditText;
    private Button chooseImageButton;
    private Button uploadImageButton;
    private Uri imageUri; // Store the selected image URI
    private String uid;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    private ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    // Handle the selected image data here
                    handlePickedImage(data, getContext());

                }
            }
    );

    private final ActivityResultLauncher<Intent> videoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Retrieve the selected video URI
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri videoUri = data.getData();

                        // Check the size of the selected video
                        String videoPath = getRealPathFromURI(videoUri);

                        if (videoPath != null && isFileSizeValid(videoPath, 5)) {
                            // Proceed with uploading the video
                            Intent i = new Intent(getContext(), Upload_Reel.class);
                            i.setData(videoUri);
                            startActivity(i);
                        } else {
                            // Notify the user that the video exceeds the maximum allowed size
                            Toast.makeText(getContext(), "Video size exceeds the maximum allowed limit of 5mb", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
    );
    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }

    public static boolean isFileSizeValid(String videoFilePath, int maxSizeInMB) {
        File videoFile = new File(videoFilePath);
        long fileSizeInBytes = videoFile.length();
        long fileSizeInMB = fileSizeInBytes / (1024 * 1024); // Convert bytes to megabytes

        return fileSizeInMB <= maxSizeInMB;
    }
    private void handlePickedImage(Intent data, Context context) {
        Uri imageUri = data.getData();
        Intent intent = new Intent(context, UploadStoryActivity.class);
        intent.putExtra("IMAGE_URI", imageUri.toString());
        context.startActivity(intent);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();

        addPost=view.findViewById(R.id.addPost);
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(getContext(), Uploads.class);
                startActivity(intent);
            }
        });
        addStory=view.findViewById(R.id.addStory);
        addStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                imageActivityResultLauncher.launch(intent);
            }
        });
        addReel=view.findViewById(R.id.addReel);
        addReel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectVideo();



            }
        });
        return view;
/*
        imagePreview = view.findViewById(R.id.imagePreview);
        captionEditText = view.findViewById(R.id.captionEditText);
        chooseImageButton = view.findViewById(R.id.chooseImageButton);
        uploadImageButton = view.findViewById(R.id.uploadImageButton);
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("uid",uid);

        chooseImageButton.setOnClickListener(v -> openFileChooser());

        uploadImageButton.setOnClickListener(v -> uploadImage());

        // Initialize Activity Result Launcher for image selection
        imageActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                imagePreview.setImageURI(imageUri);
            }
        });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imageActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));

    }

    private void uploadImage() {
        if (imageUri != null) {
            // Show ProgressBar to indicate progress
            ProgressBar progressBar = requireView().findViewById(R.id.progressBar);
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
                        String userId=uid;
                    Map<String, Object> postDetails = new HashMap<>();
                    postDetails.put("imageURL", imageUrl);
                    postDetails.put("caption",String.valueOf(captionEditText.getText()));
                    String postId=databaseRef.push().getKey();
                    if (postId != null) {
                        databaseRef.child(postId).setValue(postDetails)
                                .addOnSuccessListener(aVoid -> {
                                    // Data successfully added to the database
                                    // You can display a success message or take further actions here
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Post has been uploaded.", Toast.LENGTH_SHORT).show();
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
        }*/

    }

    private void selectVideo() {
        // Create an intent to pick a video from the device
        Intent pickVideoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        // Launch the intent with the ActivityResultLauncher
        videoPickerLauncher.launch(pickVideoIntent);
    }

    private void startHomeFragment() {
        // Navigate to HomeFragment
        Intent i = new Intent(getContext(),MainActivity.class);
        startActivity(i);
    }

}
