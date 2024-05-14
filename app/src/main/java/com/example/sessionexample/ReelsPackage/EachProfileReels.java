package com.example.sessionexample.ReelsPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.sessionexample.R;

import java.util.ArrayList;
import java.util.List;

public class EachProfileReels extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private ArrayList<String> videoList,videoListId,caption;
    String userId,username,userProfile;
    int position;
    private List<ReelsModel> videoListModelData;
    private ReelsAdapter reelsAdapter;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_profile_reels);


        videoList = getIntent().getStringArrayListExtra("VideoUrls");
        videoListId = getIntent().getStringArrayListExtra("VideoUrlId");
        caption = getIntent().getStringArrayListExtra("caption");
        userId = getIntent().getStringExtra("myProfileId");
        username = getIntent().getStringExtra("username");
        userProfile = getIntent().getStringExtra("userProfile");
        currentUsername = getIntent().getStringExtra("currentUsername");
        String tempPosition = getIntent().getStringExtra("position");
        position = Integer.parseInt(tempPosition);

        videoListModelData = new ArrayList<>();

        viewPager2 = findViewById(R.id.viewPager2);

        for(int i=0;i<videoList.size();i++){

            String videoCaption = caption.get(i);

            if(videoCaption == null || videoCaption.isEmpty()){
                videoCaption = "";
            }

            videoListModelData.add(new ReelsModel(videoList.get(i),username,videoCaption,videoListId.get(i),userId,userProfile,currentUsername));

        }

        reelsAdapter = new ReelsAdapter(this,videoListModelData);
        viewPager2.setAdapter(reelsAdapter);

        scrollToPosition(position);


    }

    private void scrollToPosition(int position) {
        // Check if the position is valid
        if (position >= 0 && position < viewPager2.getAdapter().getItemCount()) {
            // Scroll to the specified position
            viewPager2.setCurrentItem(position, true); // Set smoothScroll to true for smooth scrolling
        }
    }

}