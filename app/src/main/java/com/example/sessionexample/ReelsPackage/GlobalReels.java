package com.example.sessionexample.ReelsPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.sessionexample.R;

import java.util.ArrayList;
import java.util.List;

public class GlobalReels extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private ArrayList<String> videoList,videoListId,videoListUserId,username,userProfile,caption;
    private List<ReelsModel> videoListModelData;
    private ReelsAdapter reelsAdapter;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_reels);

        videoList = getIntent().getStringArrayListExtra("videoUrl");
        videoListId = getIntent().getStringArrayListExtra("videoUrlId");
        videoListUserId = getIntent().getStringArrayListExtra("videoUrlUserId");
        username = getIntent().getStringArrayListExtra("username");
        userProfile = getIntent().getStringArrayListExtra("userProfile");
        caption = getIntent().getStringArrayListExtra("caption");
        currentUsername = getIntent().getStringExtra("currentUsername");

        videoListModelData = new ArrayList<>();

        viewPager2 = findViewById(R.id.viewPager2);

        for(int i=0;i<videoList.size();i++){

            String videoCaption = caption.get(i);
            if(videoCaption == null || videoCaption.isEmpty())
                videoCaption = "";

            videoListModelData.add(new ReelsModel(videoList.get(i),username.get(i),videoCaption ,videoListId.get(i),videoListUserId.get(i),userProfile.get(i),currentUsername));

        }

        reelsAdapter = new ReelsAdapter(this,videoListModelData);
        viewPager2.setAdapter(reelsAdapter);



    }
}