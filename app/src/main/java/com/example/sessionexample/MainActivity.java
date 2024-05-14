package com.example.sessionexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sessionexample.ReelsPackage.GlobalReels;
import com.example.sessionexample.reels.addReel;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    String moveToSearchFragment,moveToAddPostFragment,currentUsername;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<String> videoURL,videoURLID,userId,username,userProfile,caption;
    ArrayList<String> tempVideoUrl,tempVideoUrlId,tempUserId,tempUsername,tempUserProfile,tempCaption;
    addReel addReel;
    private static final long BACK_PRESS_TIME_INTERVAL = 4000; // 2 seconds interval
    private long backPressTime;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu,menu);

        // Get each menu item and customize its icon size
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            customizeMenuItemIconSize(menuItem, 100, 32);
        }

        return true;
    }

    private void customizeMenuItemIconSize(MenuItem menuItem, int width, int height) {
        Drawable icon = menuItem.getIcon();
        if (icon != null) {
            icon.setBounds(0, 0, width, height);
            menuItem.setIcon(icon);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String moveToHomeFragment=getIntent().getStringExtra("moveToHomeFragment");

        moveToSearchFragment = getIntent().getStringExtra("moveToSearchFragment");
        moveToAddPostFragment = getIntent().getStringExtra("moveToAddPostFragment");
        String moveToMyProfile = getIntent().getStringExtra("moveToMyProfile");

        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();

        videoURL = new ArrayList<>();
        videoURLID = new ArrayList<>();
        userId = new ArrayList<>();
        username = new ArrayList<>();
        userProfile = new ArrayList<>();
        caption = new ArrayList<>();
        currentUsername = "";


        DatabaseReference reelsRef = databaseReference.child("reels");
        reelsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    int totalChildren = (int) dataSnapshot.getChildrenCount();
                    final int[] counter = {0};

                    for(DataSnapshot userIdRef : dataSnapshot.getChildren()){

                        counter[0]++;

                        String id = userIdRef.getKey();

                        DatabaseReference usersRefForStatus = databaseReference.child("Users").child(id);
                        usersRefForStatus.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String uname = dataSnapshot.child("name").getValue(String.class);

                                if(id.equals(mAuth.getUid())){

                                    currentUsername = uname;

                                }

                                String userPhotoUrl = dataSnapshot.child("profileImage").getValue(String.class);

                                String status = dataSnapshot.child("status").getValue(String.class);

                                if(status.equals("public")){

                                    for(DataSnapshot reelIdRef : userIdRef.getChildren()){

                                        username.add(uname);

                                        userProfile.add(userPhotoUrl);

                                        userId.add(id);

                                        String reelId = reelIdRef.getKey();

                                        videoURLID.add(reelId);

                                        String reelURL = reelIdRef.child("videoURL").getValue(String.class);

                                        videoURL.add(reelURL);

                                        String videoCaption = reelIdRef.child("caption").getValue(String.class);

                                        caption.add(videoCaption);
                                    }

                                    if(counter[0] == totalChildren){

                                        // Generate a list of indices
                                        List<Integer> indices = new ArrayList<>();
                                        for (int i = 0; i < videoURL.size(); i++) {
                                            indices.add(i);
                                        }

                                        // Shuffle the list of indices
                                        Collections.shuffle(indices);

                                        // Create shuffled lists based on shuffled indices
                                        tempVideoUrl = new ArrayList<>();
                                        tempVideoUrlId = new ArrayList<>();
                                        tempUserId = new ArrayList<>();
                                        tempUsername = new ArrayList<>();
                                        tempUserProfile = new ArrayList<>();
                                        tempCaption = new ArrayList<>();

                                        for (int index : indices) {
                                            tempVideoUrl.add(videoURL.get(index));
                                            tempVideoUrlId.add(videoURLID.get(index));
                                            tempUserId.add(userId.get(index));
                                            tempUsername.add(username.get(index));
                                            tempUserProfile.add(userProfile.get(index));
                                            tempCaption.add(caption.get(index));
                                        }

                                        addReel = com.example.sessionexample.reels.addReel.newInstance(tempVideoUrl,tempVideoUrlId,tempUserId);



                                    }


                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if(moveToSearchFragment != null)
        {
            bottomNavigationView.setSelectedItemId(R.id.search);
        }
        else if(moveToAddPostFragment != null)
        {
            bottomNavigationView.setSelectedItemId(R.id.add);
        }
        else if (moveToHomeFragment!=null) {
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
        else if(moveToMyProfile != null)
        {
            bottomNavigationView.setSelectedItemId(R.id.person);
        }
        else
        {
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }

    @Override
    public void onBackPressed() {
        // Handle back button press in the activity
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check if there are any fragments in the back stack
        if (fragmentManager.getBackStackEntryCount() > 1) {
            // Pop the fragment from the back stack
            fragmentManager.popBackStack();

        } else if (fragmentManager.getBackStackEntryCount() == 1) {
            // If there is only 1 fragment in the back stack, exit the app
            // If no fragments in the back stack
            if (backPressTime + BACK_PRESS_TIME_INTERVAL > System.currentTimeMillis()) {
                // If back button pressed again within the interval, exit the application
                finish();
                super.onBackPressed();
            } else {
                // Otherwise, show "Press again to exit" message
                Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            }
            // Update back press time
            backPressTime = System.currentTimeMillis();

        }
    }

    private void moveToLoginActivity() {
        Intent i = new Intent(MainActivity.this,LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    HomeFragment homeFragment = new HomeFragment();
    SearchFragment searchFragment = new SearchFragment();
    globalPosts globalPosts = new globalPosts();
    AddPost addPost = new AddPost();
    MyProfile myProfile = new MyProfile();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.person)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, myProfile).addToBackStack(null).commit();

            return true;
        }
        else if(item.getItemId() == R.id.home)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, homeFragment).addToBackStack(null).commit();

            return true;
        }
        else if(item.getItemId() == R.id.search)
        {
            if(moveToSearchFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, searchFragment).addToBackStack(null).commit();

            }
            else {
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, globalPosts).addToBackStack(null).commit();

            }
            return true;
        }
        else if(item.getItemId() == R.id.add)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, addPost).addToBackStack(null).commit();

            return true;
        }
        else if(item.getItemId() == R.id.reels)
        {
            if(videoURL.size() != 0){

                Intent i = new Intent(MainActivity.this, GlobalReels.class);
                i.putStringArrayListExtra("videoUrl",tempVideoUrl);
                i.putStringArrayListExtra("videoUrlId",tempVideoUrlId);
                i.putStringArrayListExtra("videoUrlUserId",tempUserId);
                i.putStringArrayListExtra("username",tempUsername);
                i.putStringArrayListExtra("userProfile",tempUserProfile);
                i.putStringArrayListExtra("caption",tempCaption);
                i.putExtra("currentUsername",currentUsername);
                startActivity(i);

                //getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, addReel).addToBackStack(null).commit();
                //return true;

            }
            else{
                Toast.makeText(this, "No Public Reels available", Toast.LENGTH_LONG).show();
            }


        }
        return false;
    }
}