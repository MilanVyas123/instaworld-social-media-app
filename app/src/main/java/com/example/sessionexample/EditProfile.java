package com.example.sessionexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    FirebaseAuth mAuth;

    FirebaseDatabase database;

    DatabaseReference databaseReference;

    String uid,email,originalUsername;

    EditText username,bio;

    TextView updateUserProfile;

    ImageView userProfile;

    StorageReference storageReference;

    Uri imageUri;

    String originalImageUrl;

    CircleImageView userProfileImage;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        username = findViewById(R.id.username);
        bio = findViewById(R.id.userBIO);
        userProfileImage = findViewById(R.id.userProfileImage);

        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        uid = mAuth.getUid();

        DatabaseReference usersRef = databaseReference.child("Users");
        usersRef.child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.getValue(String.class);
                    originalUsername = dataSnapshot.getValue(String.class);
                    // Now you can use the user's name
                    username.setText(userName);
                } else {
                    // Handle the case where the data doesn't exist
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
            }
        });

        usersRef.child(uid).child("bio").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String Bio = dataSnapshot.getValue(String.class);
                    // Now you can use the user's name
                    bio.setText(Bio);
                } else {
                    // Handle the case where the data doesn't exist
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
            }
        });

        usersRef.child(uid).child("profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    originalImageUrl = dataSnapshot.getValue(String.class);

                    Picasso.get().load(originalImageUrl).into(userProfileImage);

                } else {
                    // Handle the case where the data doesn't exist
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
            }
        });


        usersRef.child(uid).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    email = dataSnapshot.getValue(String.class);
                    // Now you can use the user's name
                } else {
                    // Handle the case where the data doesn't exist
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
            }
        });

        updateUserProfile = findViewById(R.id.editprofileImage);
        userProfile = findViewById(R.id.userProfileImage);

    }

    public void saveProfile(View view) {

        String name = username.getText().toString();
        String userBio = bio.getText().toString();

        if(name.isEmpty() || name.trim().isEmpty())
        {
            Toast.makeText(EditProfile.this,"Invalid Username",Toast.LENGTH_LONG).show();
        }
        else
        {
           checkRegisteredUsername(name,userBio);
        }

    }

    private void checkRegisteredUsername(String name,String userBio) {

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference usersRef = databaseReference.child("Users");
        usersRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    if(originalUsername.equals(name)) {

                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                        if(imageUri != null)
                        {
                            StorageReference imagesRef = storageRef.child("images").child(uid).child(uid + ".jpg");

                            UploadTask uploadTask = imagesRef.putFile(imageUri);

                            uploadTask.addOnSuccessListener(taskSnapshot -> {
                                // Image uploaded successfully
                                imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();
                                    // Save the image URL in the Firebase Realtime Database
                                    User user = new User();
                                    user.setName(name);
                                    user.setEmail(email);
                                    user.setBio(userBio);
                                    user.setProfileImage(imageUrl);

                                    usersRef.child(uid).setValue(user);

                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(EditProfile.this,"Profile Saved",Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(EditProfile.this,MyProfile.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);

                                });
                            }).addOnFailureListener(e -> {
                                // Handle the upload failure
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(EditProfile.this, "Upload failed.", Toast.LENGTH_SHORT).show();
                            });
                        }
                        else
                        {
                            User user = new User();
                            user.setName(name);
                            user.setEmail(email);
                            user.setBio(userBio);
                            user.setProfileImage(originalImageUrl);

                            usersRef.child(uid).setValue(user);

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditProfile.this,"Profile Saved",Toast.LENGTH_LONG).show();
                            Intent i = new Intent(EditProfile.this,MyProfile.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }

                    }
                    else
                    {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(EditProfile.this,"Username is already registered",Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                        if(imageUri != null)
                        {
                            StorageReference imagesRef = storageRef.child("images").child(uid + ".jpg");
                            UploadTask uploadTask = imagesRef.putFile(imageUri);

                            uploadTask.addOnSuccessListener(taskSnapshot -> {
                                // Image uploaded successfully
                                imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();
                                    // Save the image URL in the Firebase Realtime Database
                                    User user = new User();
                                    user.setName(name);
                                    user.setEmail(email);
                                    user.setBio(userBio);
                                    user.setProfileImage(imageUrl);

                                    usersRef.child(uid).setValue(user);

                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(EditProfile.this,"Profile Saved",Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(EditProfile.this,MyProfile.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);

                                });
                            }).addOnFailureListener(e -> {
                                // Handle the upload failure
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(EditProfile.this, "Upload failed.", Toast.LENGTH_SHORT).show();
                            });

                        }
                        else
                        {
                            User user = new User();
                            user.setName(name);
                            user.setEmail(email);
                            user.setBio(userBio);
                            user.setProfileImage(originalImageUrl);

                            usersRef.child(uid).setValue(user);

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditProfile.this,"Profile Saved",Toast.LENGTH_LONG).show();
                            Intent i = new Intent(EditProfile.this,MyProfile.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void updateUserProfileImage(View view) {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            userProfile.setImageURI(imageUri);
        }
    }

}