package com.example.sessionexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.ScrollCaptureCallback;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostDetailScrollActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<String> imageUris,postIds,globalPostIds;
    String firstImage,firstImageUserId;
    private PostDataAdapter postDataAdapter;

    @Override
    public void onBackPressed() {

        setResult(RESULT_OK);
        super.onBackPressed();
        
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail_scroll);

        // for GLOBAL POSTS
        firstImage = getIntent().getStringExtra("firstImage");
        firstImageUserId = getIntent().getStringExtra("firstImageUserId");
        globalPostIds = getIntent().getStringArrayListExtra("globalpostids");

        // for SPECIFIC USER PROFILE POSTS
        imageUris = getIntent().getStringArrayListExtra("imageUris");
        postIds = getIntent().getStringArrayListExtra("postids");
        int position = getIntent().getIntExtra("position", 0);
        String userId = getIntent().getStringExtra("userId");

        //String imagePath = imageUris.get(position);

        recyclerView = findViewById(R.id.userPosts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(PostDetailScrollActivity.this));

        if(firstImage == null && firstImageUserId == null && globalPostIds == null)
        {
            postDataAdapter = new PostDataAdapter(PostDetailScrollActivity.this,imageUris,postIds,userId);
            recyclerView.setAdapter(postDataAdapter);
            scrollToPosition(position);
        }
        else
        {
            TextView explore = findViewById(R.id.posts);
            explore.setText("Explore");
            postDataAdapter = new PostDataAdapter(PostDetailScrollActivity.this,firstImage,firstImageUserId,globalPostIds);
            recyclerView.setAdapter(postDataAdapter);
        }

    }
    private void scrollToPosition(int position) {
        // Check if the position is valid
        if (position >= 0 && position < postDataAdapter.getItemCount()) {
            // Scroll to the specified position
            recyclerView.getLayoutManager().scrollToPosition(position);
        }
    }
}