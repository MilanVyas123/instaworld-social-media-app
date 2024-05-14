package com.example.sessionexample.reels;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.sessionexample.PostDetailScrollActivity;
import com.example.sessionexample.R;
import com.example.sessionexample.ReelsPackage.EachProfileReels;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReelAdapter extends BaseAdapter {

    private Context context;
    private List<String> videoUrls,reelIDs,caption;
    private String myProfileId,username,userProfile,currentUsername;

    // here myProfileId means Id of my profile and (id of another user also that comes from viewUserProfile)

    public ReelAdapter(Context context,String myProfileId,String username,String userProfile,String currentUsername){

        this.context = context;
        videoUrls = new ArrayList<>();
        reelIDs = new ArrayList<>();
        caption = new ArrayList<>();
        this.myProfileId = myProfileId;
        this.username = username;
        this.userProfile = userProfile;
        this.currentUsername = currentUsername;

    }

    public ReelAdapter(Context context, String myProfileId) {
        this.context = context;
        this.myProfileId = myProfileId;
        videoUrls = new ArrayList<>();
        reelIDs = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return videoUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return videoUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CardView cardView;
        if (convertView == null) {
            cardView = (CardView) LayoutInflater.from(context).inflate(R.layout.reel_grid_item_adapter, parent, false);
        } else {
            cardView = (CardView) convertView;
        }

        ImageView imageView = cardView.findViewById(R.id.gridVideoView);

        String videoUrl = videoUrls.get(position);
        Bitmap thumbnail = null;
        try {
            thumbnail = getVideoThumbnail(videoUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (thumbnail != null) {
            imageView.setImageBitmap(thumbnail);
        } else {
            // Default thumbnail or error handling
            imageView.setImageResource(R.drawable.baseline_video_file_24);
        }


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, EachProfileReels.class);
                intent.putStringArrayListExtra("VideoUrls", (ArrayList<String>) videoUrls);
                intent.putStringArrayListExtra("VideoUrlId", (ArrayList<String>) reelIDs);
                intent.putStringArrayListExtra("caption",(ArrayList<String>) caption);
                intent.putExtra("position", String.valueOf(position));
                intent.putExtra("myProfileId",myProfileId);
                intent.putExtra("username",username);
                intent.putExtra("userProfile",userProfile);
                intent.putExtra("currentUsername",currentUsername);
                context.startActivity(intent);
            }
        });


        return cardView;
    }

    private Bitmap getVideoThumbnail(String videoUrl) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            // Set data source to the video URL
            retriever.setDataSource(videoUrl, new HashMap<>());
            // Retrieve frame at the specified time (e.g., first frame)
            return retriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Release the MediaMetadataRetriever
            if (retriever != null) {
                retriever.release();
            }
        }
        return null;
    }

    public void addVideoUrl(String videoUrl) {
        videoUrls.add(videoUrl);
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    public void addCaptions(String videoCaption){
        caption.add(videoCaption);
    }

    // here postId means Reel id... by mistake i took same name
    public void addPostIds(String postId)
    {
        reelIDs.add(postId);
    }


}
