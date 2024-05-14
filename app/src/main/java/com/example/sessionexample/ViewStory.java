package com.example.sessionexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;
public class ViewStory extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private static int PROGRESS_COUNT = 6;
    private StoriesProgressView storiesProgressView;
    private ImageView imageView;
    private int counter = 0;

    private String[] resourses;


    long limit = 500L;
    long pressTime = 0L;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };
    CircleImageView storyProfileImage;
    TextView storyProfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);
        storyProfileName=findViewById(R.id.storyProfileName);
        storyProfileImage=findViewById(R.id.storyProfileImage);
        storyProfileImage.setBorderWidth(2);


        String userKey = getIntent().getStringExtra("userkey");
        DatabaseReference userReference=FirebaseDatabase.getInstance().getReference("Users");
        userReference.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue(String.class);
                String imageUrl=dataSnapshot.child("profileImage").getValue(String.class);
                storyProfileName.setText(name);
                Picasso.get().load(imageUrl).into(storyProfileImage);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("story");
        databaseReference.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                PROGRESS_COUNT=count;
                resourses = new String[count];
                int i = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String storyURL = dataSnapshot1.child("storyURL").getValue(String.class);
                    resourses[i++] = storyURL;
                }

                initializeStories();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if needed
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        storiesProgressView = findViewById(R.id.stories);
        imageView = findViewById(R.id.image);

        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
    }

    private void initializeStories() {
        storiesProgressView.setBackgroundColor(getResources().getColor(R.color.storyprogressBackColor));
        storiesProgressView.setStoriesCount(PROGRESS_COUNT);
        storiesProgressView.setStoryDuration(15000L);
        storiesProgressView.setStoriesListener(this);
        counter = 0;
        storiesProgressView.startStories(0);
        Picasso.get().load(resourses[counter]).into(imageView);
    }

    @Override
    public void onNext() {
        if (resourses != null && counter < resourses.length - 1) {
            counter++;
            Picasso.get().load(resourses[counter]).into(imageView);
        } else {
            // Handle completion or loop back to the first story
            counter = 0;
            // You may want to add a custom completion handling here
        }
    }

    @Override
    public void onPrev() {
        if (resourses != null && counter > 0) {
            counter--;
            Picasso.get().load(resourses[counter]).into(imageView);
        } else {
            // Handle going back from the first story
        }
    }

    @Override
    public void onComplete() {
        // Handle completion, if needed
      //  Toast.makeText(this, "Complated", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, HomeFragment.class);


        // Start the new activity
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storiesProgressView.destroy();
    }
}
