package com.example.sessionexample.reels;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.sessionexample.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class addReel extends Fragment {

    private TextureView textureView;
    private MediaPlayer mediaPlayer;
    ProgressBar progressBar;
    private boolean isPlaying = false;
    private GestureDetector gestureDetector;
    private boolean scrollDetected = false;
    private int currentVideoIndex = 0;
    private long lastScrollTime = 0;
    private static final long SCROLL_THRESHOLD = 100;
    ImageView likeIcon,commentIcon;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<String> videoUrl,videoUrlId,userId;
    boolean firstVideoInitialized = false;

    // addReel is for GLOBALS REELS means will SHOW ALL REELS OF ALL USERS

    public addReel() {
        // Required empty public constructor
    }

    // Constructor with parameters
    public static addReel newInstance(ArrayList<String> parameter1,ArrayList<String> parameter2,ArrayList<String> parameter3) {
        addReel fragment = new addReel();
        Bundle args = new Bundle();
        args.putStringArrayList("param1", parameter1);
        args.putStringArrayList("param2", parameter2);
        args.putStringArrayList("param3", parameter3);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_reel, container, false);

        if (getArguments() != null) {

            videoUrl = getArguments().getStringArrayList("param1");

            videoUrlId = getArguments().getStringArrayList("param2");

            userId = getArguments().getStringArrayList("param3");

            //Toast.makeText(getContext(),String.valueOf(userId.size()),Toast.LENGTH_LONG).show();

        }

        // Initialize GestureDetector
        gestureDetector = new GestureDetector(getContext(), new MyGestureListener());

        textureView = view.findViewById(R.id.textureView);

        progressBar = view.findViewById(R.id.progressBar);

        likeIcon = view.findViewById(R.id.likeIcon);

        commentIcon = view.findViewById(R.id.commentIcon);

        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();

        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* to get video url
                    url = videoUrl.get(currentVideoIndex)
                 */

                /* to get video url id
                    id = videoUrlId.get(currentVideoIndex)
                */

                /* to get user id : user_id = userId.get(currentVideoIndex) */

                Toast.makeText(getContext(), String.valueOf(currentVideoIndex) + " Liked", Toast.LENGTH_SHORT).show();
            }
        });

        commentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), String.valueOf(currentVideoIndex) + " commented", Toast.LENGTH_SHORT).show();
            }
        });

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

                // Initialize MediaPlayer and start playing the first video
                initializeMediaPlayer(videoUrl.get(currentVideoIndex), surface);


            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                // Ignored, the video size is set when the video is prepared
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                // Clean up
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                // Invoked every time there's a new frame available to draw
            }
        });


        textureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        return view;
    }

    private void initializeMediaPlayer(String videoUrl, SurfaceTexture surface) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.setSurface(new Surface(surface));
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    // Calculate the aspect ratio of the video
                    int videoWidth = mediaPlayer.getVideoWidth();
                    int videoHeight = mediaPlayer.getVideoHeight();
                    float videoAspectRatio = (float) videoWidth / videoHeight;

                    // Calculate the aspect ratio of the TextureView
                    int viewWidth = textureView.getWidth();
                    int viewHeight = textureView.getHeight();
                    float viewAspectRatio = (float) viewWidth / viewHeight;

                    // Determine scale factors to fit the video width within the TextureView
                    float scaleX = 1.0f;
                    float scaleY = 1.0f;
                    if (videoAspectRatio > viewAspectRatio) {
                        // Video is wider than TextureView, scale width to fit
                        scaleX = viewWidth / (float) videoWidth;
                    } else {
                        // Video is taller than TextureView, scale height to fit
                        scaleY = videoHeight / (float) viewHeight;
                    }

                    // Apply transformation matrix to the TextureView
                    Matrix matrix = new Matrix();
                    matrix.setScale(scaleX, scaleY, viewWidth / 2f, viewHeight / 2f);
                    textureView.setTransform(matrix);


                    mediaPlayer.start();
                    isPlaying = true;
                }
            });

            //mediaPlayer.prepareAsync(); // Prepare asynchronously to avoid blocking the UI thread

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void togglePlayback() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            Toast.makeText(getContext(), "Video Paused", Toast.LENGTH_SHORT).show();
            isPlaying = false;
        } else if (mediaPlayer != null) {
            mediaPlayer.start();
            Toast.makeText(getContext(), "Video Resumed", Toast.LENGTH_SHORT).show();
            isPlaying = true;
        }
    }

    private void changeVideo(boolean next) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (next) {
            currentVideoIndex = (currentVideoIndex + 1) % videoUrl.size();

            Log.d("CurrentVideoIndex:Next", String.valueOf(currentVideoIndex));

        } else  {
            currentVideoIndex = (currentVideoIndex - 1 + videoUrl.size()) % videoUrl.size();

            Log.d("CurrentVideoIndex:Previous", String.valueOf(currentVideoIndex));
        }

        //initializeMediaPlayer(videoUrl[currentVideoIndex], textureView.getSurfaceTexture());


        // Fade animation
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(500); // Adjust the duration as needed
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
                showLoadingView();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended
                hideLoadingView();
                initializeMediaPlayer(videoUrl.get(currentVideoIndex), textureView.getSurfaceTexture());

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated
            }
        });

        // Apply the animation to the TextureView
        textureView.startAnimation(alphaAnimation);
    }

    private void showLoadingView() {
        // Show loading view (e.g., a ProgressBar)
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingView() {
        // Hide loading view (e.g., a ProgressBar)
        progressBar.setVisibility(View.GONE);
    }



    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // Handle single tap (click) event
            togglePlayback();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            long currentTime = System.currentTimeMillis();

            // Check if it's been enough time since the last scroll action
            if (currentTime - lastScrollTime < SCROLL_THRESHOLD) {
                return true; // Ignore this scroll action
            }

            lastScrollTime = currentTime;

            // Handle scroll event


            if (!scrollDetected) {
                // Add your code for scroll handling here
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    // Horizontal scroll
                    if (distanceX > 0) {
                        //Toast.makeText(getContext(), "Right Scroll", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(getContext(), "Left Scroll", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Vertical scroll
                    if (distanceY > 0) {

                        if(currentVideoIndex != videoUrl.size()-1)
                            changeVideo(true);
                        //Toast.makeText(getContext(), "Down Scroll", Toast.LENGTH_SHORT).show();
                    } else {
                        if(currentVideoIndex != 0)
                            changeVideo(false);
                        //Toast.makeText(getActivity(), "Up Scroll", Toast.LENGTH_SHORT).show();
                    }
                }

                scrollDetected = true;
            }

            else{

                scrollDetected = false;

            }


            return true;
        }
    }
}


