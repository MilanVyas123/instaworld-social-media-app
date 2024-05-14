package com.example.sessionexample.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sessionexample.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

// CommentAdapter.java
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private Context context;
    private DatabaseReference userReference;

    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context=context;
        userReference=FirebaseDatabase.getInstance().getReference("Users");
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        userReference.child(comment.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> userData = (Map<String, Object>) dataSnapshot.getValue();

                holder.userNameTextView.setText((String)userData.get("name"));
                Picasso.get().load((String)userData.get("profileImage")).into(holder.profileImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    //    holder.userNameTextView.setText(comment.getUserName());
       holder.commentTextView.setText(comment.getCommentText());
       // Toast.makeText(context, String.valueOf(getItemCount()), Toast.LENGTH_SHORT).show();

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
    // CommentViewHolder.java
    public class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameTextView;
        public TextView commentTextView;

        public CircleImageView profileImage;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            profileImage=itemView.findViewById(R.id.profileImageView);
        }
    }

}

