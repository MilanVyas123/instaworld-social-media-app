package com.example.sessionexample.help;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sessionexample.LoginActivity;
import com.example.sessionexample.LogoDisplay;
import com.example.sessionexample.R;
import com.example.sessionexample.Register;
import com.example.sessionexample.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.StartupTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class DeleteAccount extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ProgressBar progressBar5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();

        progressBar5 = findViewById(R.id.progressBar5);
    }
    public void deleteAccount(View view) {

        startDeletetion();

    }

    private void deletePostComments(){

        DatabaseReference commentsRef = databaseReference.child("comments");
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot userId : dataSnapshot.getChildren()){

                    for (DataSnapshot postId : userId.getChildren()){

                        for (DataSnapshot currentUserId : postId.getChildren()){

                            if(mAuth.getUid().equals(currentUserId.getKey())){

                                DatabaseReference commentRef = databaseReference.child("comments").child(userId.getKey()).child(postId.getKey()).child(currentUserId.getKey());
                                commentRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        //Toast.makeText(DeleteAccount.this,"comment deleted",Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        //Toast.makeText(DeleteAccount.this,"comment deleted failed",Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }

                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(DeleteAccount.this,"comment failed",Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void deleteReelComment(){

        DatabaseReference commentsRef = databaseReference.child("commentsForReels");
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot userId : dataSnapshot.getChildren()){

                    for (DataSnapshot reelId : userId.getChildren()){

                        for (DataSnapshot currentUserId : reelId.getChildren()){

                            if(mAuth.getUid().equals(currentUserId.getKey())){

                                DatabaseReference commentRef = databaseReference.child("commentsForReels").child(userId.getKey()).child(reelId.getKey()).child(currentUserId.getKey());
                                commentRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        //Toast.makeText(DeleteAccount.this,"comment deleted",Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        //Toast.makeText(DeleteAccount.this,"comment deleted failed",Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }

                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                //Toast.makeText(DeleteAccount.this,"comment failed",Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void deleteFromFollowersForFollowers(){

        DatabaseReference followersRef = databaseReference.child("followers").child(mAuth.getUid());
        followersRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                //Toast.makeText(DeleteAccount.this,"followers deleted",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                //Toast.makeText(DeleteAccount.this,"followers deletion failed",Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void deleteFromFollowingForFollowers(){

        DatabaseReference followingRef = databaseReference.child("following");
        followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userId : dataSnapshot.getChildren()){

                    for (DataSnapshot childUserId : userId.getChildren()){

                        if(childUserId.getKey().equals(mAuth.getUid())){

                            DatabaseReference deleteFollowingRef = databaseReference.child("following").child(userId.getKey()).child(childUserId.getKey());
                            deleteFollowingRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

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

    private void deleteFromFollowersForFollowing(){

        DatabaseReference followersRef  = databaseReference.child("followers");
        followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userId : dataSnapshot.getChildren()){

                    for (DataSnapshot childUserId : userId.getChildren()){

                        if(childUserId.getKey().equals(mAuth.getUid())){

                            DatabaseReference deleteFollowersRef = databaseReference.child("followers").child(userId.getKey()).child(childUserId.getKey());
                            deleteFollowersRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

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

    private void deleteFromFollowingForFollowing(){

        DatabaseReference followingRef = databaseReference.child("following").child(mAuth.getUid());
        followingRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                //Toast.makeText(DeleteAccount.this,"following deleted",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                //Toast.makeText(DeleteAccount.this,"following deletion failed",Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void deletePostLikes(){

        DatabaseReference postLikesRef  = databaseReference.child("likes");
        postLikesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userId : dataSnapshot.getChildren()){

                    for (DataSnapshot postId : userId.getChildren()){

                        for (DataSnapshot childUserId : postId.getChildren()){

                            if(childUserId.getKey().equals(mAuth.getUid())){

                                DatabaseReference deletePostLikes = databaseReference.child("likes").child(userId.getKey()).child(postId.getKey()).child(childUserId.getKey());
                                deletePostLikes.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

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

    private void deleteReelsLikes(){

        DatabaseReference reelLikesRef  = databaseReference.child("likesReels");
        reelLikesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userId : dataSnapshot.getChildren()){

                    for (DataSnapshot reelId : userId.getChildren()){

                        for (DataSnapshot childUserId : reelId.getChildren()){

                            if(childUserId.getKey().equals(mAuth.getUid())){

                                DatabaseReference deleteReelLikes = databaseReference.child("likesReels").child(userId.getKey()).child(reelId.getKey()).child(childUserId.getKey());
                                deleteReelLikes.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

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

    private void deletePostLikesOfMyPost(){

        DatabaseReference likesRef = databaseReference.child("likes").child(mAuth.getUid());
        likesRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void deleteCommentLikesOfMyPost(){

        DatabaseReference commentsRef = databaseReference.child("comments").child(mAuth.getUid());
        commentsRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void deletePost(){

        DatabaseReference postsRef = databaseReference.child("posts").child(mAuth.getUid());
        postsRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void deleteReelsLikesForMyReel(){

        DatabaseReference likesRef = databaseReference.child("likesReels").child(mAuth.getUid());
        likesRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void deleteReelsCommentForMyReel(){

        DatabaseReference commentsRef = databaseReference.child("commentsForReels").child(mAuth.getUid());
        commentsRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void deleteReels(){

        DatabaseReference reelsRef = databaseReference.child("reels").child(mAuth.getUid());
        reelsRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void deleteReelsUserIdFolder(){

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("reels").child(mAuth.getUid());

        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    item.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    });
                }

                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

    }

    private void deleteFromRequest(){

        DatabaseReference requestRef = databaseReference.child("requests").child(mAuth.getUid());
        requestRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void deleteStory(){

        DatabaseReference storyRef = databaseReference.child("story").child(mAuth.getUid());
        storyRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void deleteProfileImage(){

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(mAuth.getUid());

        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    item.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {


                        }
                    });
                }

                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        deleteUser();

                        deleteUserFromFirebaseAuthentication();

                        progressBar5.setVisibility(View.GONE);

                        moveToLoginActivity();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        deleteUser();

                        deleteUserFromFirebaseAuthentication();

                        progressBar5.setVisibility(View.GONE);

                        moveToLoginActivity();





                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {


            }
        });

    }

    private void deleteUser(){

        DatabaseReference usersRef = databaseReference.child("Users").child(mAuth.getUid());
        usersRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void deleteUserFromFirebaseAuthentication() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            currentUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        } else {
            // No user is currently authenticated
            // Handle accordingly
        }
    }

    private void moveToLoginActivity()
    {
        Toast.makeText(getApplicationContext(), "Account Deleted", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getApplicationContext(), LogoDisplay.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    private void startDeletetion(){

        progressBar5.setVisibility(View.VISIBLE);

        deletePostComments();

        deleteReelComment();

        deleteFromFollowersForFollowers();

        deleteFromFollowingForFollowers();

        deleteFromFollowersForFollowing();

        deleteFromFollowingForFollowing();

        deletePostLikes();

        deleteReelsLikes();

        deletePostLikesOfMyPost();

        deleteCommentLikesOfMyPost();

        deletePost();

        deleteReelsLikesForMyReel();

        deleteReelsCommentForMyReel();

        deleteReels();

        deleteReelsUserIdFolder();

        deleteFromRequest();

        deleteStory();

        deleteProfileImage();



    }

}