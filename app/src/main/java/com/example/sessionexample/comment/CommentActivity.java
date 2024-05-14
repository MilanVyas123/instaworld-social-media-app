package com.example.sessionexample.comment;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sessionexample.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// CommentActivity.java
public class CommentActivity extends AppCompatActivity {
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private String postUserId;
    private String postId;
    private String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        postUserId=getIntent().getStringExtra("postUserId");
        postId=getIntent().getStringExtra("postId");
        state=getIntent().getStringExtra("forReels");


        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList,this);
        commentRecyclerView.setAdapter(commentAdapter);

        // Load comments from Firebase or any other data source
        loadComments();
    }

    private void loadComments() {

        // Replace this with your Firebase logic to fetch comments
        // For example, assuming you have a DatabaseReference for comments:
        if (state!=null) {
            DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("commentsForReels");
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
            commentsRef.child(postUserId).child(postId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    commentList.clear();

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        String userKey = dataSnapshot1.getKey();

                        //     Toast.makeText(CommentActivity.this, userKey, Toast.LENGTH_SHORT).show();
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                            Comment comment = new Comment();
                            comment.setUserId(userKey);
                            // Toast.makeText(CommentActivity.this, dataSnapshot2.getKey(), Toast.LENGTH_SHORT).show();
                            Map<String, Object> commentData = (Map<String, Object>) dataSnapshot2.getValue();
                            String commentText = (String) commentData.get("comment");
                            comment.setCommentText(commentText);
                            commentList.add(comment);

                        }
                    }


                    commentAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }
        else
        {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments");
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
        commentsRef.child(postUserId).child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                commentList.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String userKey = dataSnapshot1.getKey();

                    //     Toast.makeText(CommentActivity.this, userKey, Toast.LENGTH_SHORT).show();
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        Comment comment = new Comment();
                        comment.setUserId(userKey);
                        // Toast.makeText(CommentActivity.this, dataSnapshot2.getKey(), Toast.LENGTH_SHORT).show();
                        Map<String, Object> commentData = (Map<String, Object>) dataSnapshot2.getValue();
                        String commentText = (String) commentData.get("comment");
                        comment.setCommentText(commentText);
                        commentList.add(comment);

                    }
                }


                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }
    }
    public void uploadComment(View view) {
        if(state!=null)
        {
            EditText editComment = findViewById(R.id.editComment);
            String comment = editComment.getText().toString();

            String postId = getIntent().getStringExtra("postId");
            String postUserId = getIntent().getStringExtra("postUserId");

            DatabaseReference commentReference = FirebaseDatabase.getInstance().getReference("commentsForReels");

            String uid = FirebaseAuth.getInstance().getUid();
            if (postId != null) {
                // Generate a unique key for each comment using push()
                DatabaseReference newCommentRef = commentReference.child(postUserId).child(postId).child(uid).push();

                Map<String, Object> commentDetail = new HashMap<>();
                commentDetail.put("comment", comment);
                commentDetail.put("timestamp", System.currentTimeMillis());

                newCommentRef.setValue(commentDetail)
                        .addOnSuccessListener(aVoid -> {
                            editComment.getText().clear();
                            editComment.clearFocus();
                            // Data successfully added to the database
                            // You can display a success message or take further actions here
                        })
                        .addOnFailureListener(e -> {
                            // Handle any errors that occurred while adding data to the database
                        });
            }
        }
        else
        {
            EditText editComment = findViewById(R.id.editComment);
            String comment = editComment.getText().toString();

            String postId = getIntent().getStringExtra("postId");
            String postUserId = getIntent().getStringExtra("postUserId");

            DatabaseReference commentReference = FirebaseDatabase.getInstance().getReference("comments");

            String uid = FirebaseAuth.getInstance().getUid();
            if (postId != null) {
                // Generate a unique key for each comment using push()
                DatabaseReference newCommentRef = commentReference.child(postUserId).child(postId).child(uid).push();

                Map<String, Object> commentDetail = new HashMap<>();
                commentDetail.put("comment", comment);
                commentDetail.put("timestamp", System.currentTimeMillis());

                newCommentRef.setValue(commentDetail)
                        .addOnSuccessListener(aVoid -> {
                            editComment.getText().clear();
                            editComment.clearFocus();
                            // Data successfully added to the database
                            // You can display a success message or take further actions here
                        })
                        .addOnFailureListener(e -> {
                            // Handle any errors that occurred while adding data to the database
                        });
            }
        }
    }

}

