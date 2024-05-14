package com.example.sessionexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewFollowRequest extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ArrayList<Model> list;
    private String currentUserId,currentUserName;
    private FollowRequestAdapter followRequestAdapter;
    ArrayList<String> allFollowUserIds;

    @Override
    public void onBackPressed() {
        DatabaseReference followersRef = databaseReference.child("followers").child(currentUserId);
        followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String followerUserId = dataSnapshot1.getKey();
                    count++;

                    DatabaseReference requestsRef = databaseReference.child("requests").child(currentUserId);
                    requestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                String requestedId = dataSnapshot2.getKey();
                                if (requestedId.equals(followerUserId)) {
                                    DatabaseReference removeId = databaseReference.child("requests").child(currentUserId).child(requestedId);
                                    removeId.removeValue();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle onCancelled
                        }
                    });
                }

                if(count == dataSnapshot.getChildrenCount())
                    ViewFollowRequest.super.onBackPressed();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_follow_request);

        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();
        allFollowUserIds = new ArrayList<>();
        currentUserId = mAuth.getUid();
        getCurrentUsername(currentUserId);

        list = new ArrayList<>();
        followRequestAdapter = new FollowRequestAdapter(this,list);

        followRequestAdapter.setOnItemClickListener(new FollowRequestAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String action,String username) {
                if ("Accept".equals(action)) {
                    followRequestAdapter.removeItem(position);
                    followRequestAdapter.notifyItemRemoved(position);

                    DatabaseReference UsersRef = databaseReference.child("Users");
                    UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                            {
                                String userId = dataSnapshot1.getKey();

                                DatabaseReference UserRefname = databaseReference.child("Users").child(userId).child("name");
                                UserRefname.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists())
                                        {
                                            String userName = dataSnapshot.getValue(String.class);
                                            if(userName.equals(username))
                                            {
                                                DatabaseReference followersRef = database.getReference("followers").child(currentUserId).child(userId);
                                                followersRef.setValue("following");

                                                DatabaseReference followingfRef = database.getReference("following").child(userId).child(currentUserId);
                                                followingfRef.setValue("follows");

                                                //DatabaseReference requestsRef = databaseReference.child("requests").child(currentUserId).child(userId);
                                                //requestsRef.removeValue();

                                                //userid of opposite person
                                                //Toast.makeText(ViewFollowRequest.this,userId,Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //Toast.makeText(ViewFollowRequest.this, "Accept clicked at position: " + username, Toast.LENGTH_SHORT).show();
                    // Handle Accept action
                } else if ("Decline".equals(action)) {

                    DatabaseReference UsersRef = databaseReference.child("Users");
                    UsersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                            {
                                String userId = dataSnapshot1.getKey();

                                DatabaseReference UserRefname = databaseReference.child("Users").child(userId).child("name");
                                UserRefname.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists())
                                        {
                                            String userName = dataSnapshot.getValue(String.class);
                                            if(userName.equals(username))
                                            {
                                                followRequestAdapter.clear();
                                                list.clear();
                                                allFollowUserIds.clear();
                                                DatabaseReference requestsRef = databaseReference.child("requests").child(currentUserId).child(userId);
                                                requestsRef.removeValue();

                                                //userid of opposite person
                                                //Toast.makeText(ViewFollowRequest.this,userId,Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //Toast.makeText(ViewFollowRequest.this, "Decline clicked at position: " + username, Toast.LENGTH_SHORT).show();
                    // Handle Decline action
                } else if("ViewProfile".equals(action)) {

                    Intent i = new Intent(ViewFollowRequest.this,viewUserProfile.class);
                    i.putExtra("username",username);
                    i.putExtra("currentUserName",currentUserName);
                    startActivity(i);
                }
            }
        });

        recyclerView = findViewById(R.id.followRequests);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(followRequestAdapter);

        DatabaseReference checkFollowRequest = databaseReference.child("requests").child(currentUserId);
        checkFollowRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        String userId = snapshot.getKey();
                        allFollowUserIds.add(userId);
                    }
                    fillAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getCurrentUsername(String currentUserId) {
        DatabaseReference UsersData = databaseReference.child("Users").child(currentUserId).child("name");
        UsersData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fillAdapter()
    {
        for (int i=0;i<allFollowUserIds.size();i++)
        {
            DatabaseReference userData = databaseReference.child("Users").child(allFollowUserIds.get(i));
            userData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {

                            String username = dataSnapshot.child("name").getValue(String.class);
                            String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                            Model model = new Model(imageUrl,username);
                            list.add(model);

                            followRequestAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}