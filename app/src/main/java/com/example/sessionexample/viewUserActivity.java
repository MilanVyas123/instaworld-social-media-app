package com.example.sessionexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class viewUserActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ArrayList<Model> list;
    private ViewUserActivityData viewUserActivityData;
    private ViewAnotherUserActivityData viewAnotherUserActivityData;
    private ViewAnotherUserActivityData.OnFollowButtonClickListener followButtonClickListener;
    TextView textView;
    String currentUserName;
    EditText searchString;
    public viewUserActivity()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();

        String Activity = getIntent().getStringExtra("activity");
        String userID = getIntent().getStringExtra("userID");
        list = new ArrayList<>();
        textView = findViewById(R.id.TextView);
        searchString = findViewById(R.id.searchUser);

        if(!mAuth.getUid().equals(userID))
        {
            View view = LayoutInflater.from(this).inflate(R.layout.view_another_user_activity_data, null);
            followButtonClickListener = new ViewAnotherUserActivityData.OnFollowButtonClickListener() {
                @Override
                public void onFollowButtonClick(int position) {
                    String username = viewAnotherUserActivityData.getNameAtPosition(position);

                    DatabaseReference usersRef = databaseReference.child("Users");
                    usersRef.orderByChild("name").equalTo(username).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                            {
                                String key = dataSnapshot1.getKey();
                                DatabaseReference followRef = databaseReference.child("followers").child(key).child(mAuth.getUid());
                                followRef.setValue("following");
                                DatabaseReference followingRef = databaseReference.child("following").child(mAuth.getUid()).child(key);
                                followingRef.setValue("follows");
                                viewAnotherUserActivityData.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            };

            viewAnotherUserActivityData = new ViewAnotherUserActivityData(viewUserActivity.this,list,Activity,userID,followButtonClickListener);

            viewAnotherUserActivityData.setOnItemClickListener(new ViewAnotherUserActivityData.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    String username = viewAnotherUserActivityData.getNameAtPosition(position);

                    DatabaseReference databaseReference1 = databaseReference.child("Users").child(mAuth.getUid());
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            currentUserName = dataSnapshot.child("name").getValue(String.class);
                            if(currentUserName.equals(username))
                                {
                                    Intent i = new Intent(viewUserActivity.this,viewUserProfile.class);
                                    i.putExtra("username",username);
                                    i.putExtra("currentUserName",currentUserName);
                                    startActivity(i);
                                }
                                else
                                {
                                    Intent i = new Intent(viewUserActivity.this,viewUserProfile.class);
                                    i.putExtra("username",username);
                                    i.putExtra("currentUserName","");
                                    startActivity(i);
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

            recyclerView = findViewById(R.id.userProfile);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(viewUserActivity.this));
            recyclerView.setAdapter(viewAnotherUserActivityData);

            if(Activity.equals("viewUserFollowers"))
            {
                DatabaseReference followerRef = databaseReference.child("followers").child(userID);
                followerRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot followerSnapShot : dataSnapshot.getChildren())
                        {
                            String followerUserId = followerSnapShot.getKey();

                            DatabaseReference userRef =  databaseReference.child("Users").child(followerUserId);
                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists())
                                    {
                                        String username = dataSnapshot.child("name").getValue(String.class);
                                        String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                                        Model model = new Model(imageUrl,username);
                                        list.add(model);

                                        viewAnotherUserActivityData.notifyDataSetChanged();
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

                searchString.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String temp = searchString.getText().toString().toLowerCase();
                        if(temp.isEmpty() || temp.trim().isEmpty())
                        {
                            list.clear();
                            DatabaseReference followerRef = databaseReference.child("followers").child(userID);
                            followerRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for(DataSnapshot followerSnapShot : dataSnapshot.getChildren())
                                    {
                                        String followerUserId = followerSnapShot.getKey();

                                        DatabaseReference userRef =  databaseReference.child("Users").child(followerUserId);
                                        userRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists())
                                                {
                                                    String username = dataSnapshot.child("name").getValue(String.class);
                                                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                                                    Model model = new Model(imageUrl,username);
                                                    list.add(model);

                                                    viewAnotherUserActivityData.notifyDataSetChanged();
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
                        }
                        else
                        {
                            list.clear();
                            DatabaseReference followerRef = databaseReference.child("followers").child(userID);
                            followerRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for(DataSnapshot followerSnapShot : dataSnapshot.getChildren())
                                    {
                                        String followerUserId = followerSnapShot.getKey();

                                        DatabaseReference userRef =  databaseReference.child("Users").child(followerUserId);
                                        userRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists())
                                                {
                                                    String username = dataSnapshot.child("name").getValue(String.class);
                                                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                                                    if(username.toLowerCase().startsWith(temp))
                                                    {
                                                        Model model = new Model(imageUrl,username);
                                                        list.add(model);
                                                    }

                                                    viewAnotherUserActivityData.notifyDataSetChanged();
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
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            }
            else if(Activity.equals("viewUserFollowing"))
            {
                textView.setText("Following");
                DatabaseReference followerRef = databaseReference.child("following").child(userID);
                followerRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot followerSnapShot : dataSnapshot.getChildren())
                        {
                            String followerUserId = followerSnapShot.getKey();

                            DatabaseReference userRef =  databaseReference.child("Users").child(followerUserId);
                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists())
                                    {
                                        String username = dataSnapshot.child("name").getValue(String.class);
                                        String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                                        Model model = new Model(imageUrl,username);
                                        list.add(model);

                                        viewAnotherUserActivityData.notifyDataSetChanged();
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

                searchString.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String temp = searchString.getText().toString().toLowerCase();
                        if(temp.isEmpty() || temp.trim().isEmpty())
                        {
                            list.clear();
                            DatabaseReference followerRef = databaseReference.child("following").child(userID);
                            followerRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for(DataSnapshot followerSnapShot : dataSnapshot.getChildren())
                                    {
                                        String followerUserId = followerSnapShot.getKey();

                                        DatabaseReference userRef =  databaseReference.child("Users").child(followerUserId);
                                        userRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists())
                                                {
                                                    String username = dataSnapshot.child("name").getValue(String.class);
                                                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                                                    Model model = new Model(imageUrl,username);
                                                    list.add(model);

                                                    viewAnotherUserActivityData.notifyDataSetChanged();
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
                        }
                        else
                        {
                            list.clear();
                            DatabaseReference followerRef = databaseReference.child("following").child(userID);
                            followerRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for(DataSnapshot followerSnapShot : dataSnapshot.getChildren())
                                    {
                                        String followerUserId = followerSnapShot.getKey();

                                        DatabaseReference userRef =  databaseReference.child("Users").child(followerUserId);
                                        userRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists())
                                                {
                                                    String username = dataSnapshot.child("name").getValue(String.class);
                                                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                                                    if(username.toLowerCase().startsWith(temp))
                                                    {
                                                        Model model = new Model(imageUrl,username);
                                                        list.add(model);
                                                    }

                                                    viewAnotherUserActivityData.notifyDataSetChanged();
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
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

        }

        else
        {
            View view = LayoutInflater.from(this).inflate(R.layout.view_user_activity_data, null);
            viewUserActivityData = new ViewUserActivityData(viewUserActivity.this, list,Activity,userID);

            viewUserActivityData.setOnItemClickListener(new ViewUserActivityData.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    String username = viewUserActivityData.getNameAtPosition(position);

                    Intent i = new Intent(viewUserActivity.this,viewUserProfile.class);
                    i.putExtra("username",username);
                    i.putExtra("currentUserName","");
                    startActivity(i);
                }
            });

            recyclerView = findViewById(R.id.userProfile);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(viewUserActivity.this));
            recyclerView.setAdapter(viewUserActivityData);

            if(Activity.equals("viewUserFollowers"))
            {
                DatabaseReference followerRef = databaseReference.child("followers").child(userID);
                followerRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot followerSnapShot : dataSnapshot.getChildren())
                        {
                            String followerUserId = followerSnapShot.getKey();

                            DatabaseReference userRef =  databaseReference.child("Users").child(followerUserId);
                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists())
                                    {
                                        String username = dataSnapshot.child("name").getValue(String.class);
                                        String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                                        Model model = new Model(imageUrl,username);
                                        list.add(model);

                                        viewUserActivityData.notifyDataSetChanged();
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


                searchString.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String temp = searchString.getText().toString().toLowerCase();
                        if(temp.isEmpty() || temp.trim().isEmpty())
                        {
                            list.clear();
                            DatabaseReference followerRef = databaseReference.child("followers").child(userID);
                            followerRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for(DataSnapshot followerSnapShot : dataSnapshot.getChildren())
                                    {
                                        String followerUserId = followerSnapShot.getKey();

                                        DatabaseReference userRef =  databaseReference.child("Users").child(followerUserId);
                                        userRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists())
                                                {
                                                    String username = dataSnapshot.child("name").getValue(String.class);
                                                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                                                    Model model = new Model(imageUrl,username);
                                                    list.add(model);

                                                    viewUserActivityData.notifyDataSetChanged();
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

                        }
                        else
                        {
                            DatabaseReference followerRef = databaseReference.child("followers").child(userID);
                            followerRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for(DataSnapshot followerSnapShot : dataSnapshot.getChildren())
                                    {
                                        String followerUserId = followerSnapShot.getKey();

                                        DatabaseReference userRef =  databaseReference.child("Users").child(followerUserId);
                                        userRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                list.clear();
                                                if(dataSnapshot.exists())
                                                {
                                                    String username = dataSnapshot.child("name").getValue(String.class);
                                                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                                                    if(imageUrl != null && username.toLowerCase().startsWith(temp))
                                                    {
                                                        Model model = new Model(imageUrl,username);
                                                        list.add(model);
                                                    }
                                                    viewUserActivityData.notifyDataSetChanged();
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
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            }
            else if(Activity.equals("viewUserFollowing"))
            {
                textView.setText("Following");
                DatabaseReference followerRef = databaseReference.child("following").child(userID);
                followerRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot followerSnapShot : dataSnapshot.getChildren())
                        {
                            String followerUserId = followerSnapShot.getKey();

                            DatabaseReference userRef =  databaseReference.child("Users").child(followerUserId);
                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists())
                                    {
                                        String username = dataSnapshot.child("name").getValue(String.class);
                                        String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                                        Model model = new Model(imageUrl,username);
                                        list.add(model);

                                        viewUserActivityData.notifyDataSetChanged();
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

                searchString.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String temp = searchString.getText().toString().toLowerCase();
                        if(temp.isEmpty() || temp.trim().isEmpty())
                        {
                            list.clear();
                            DatabaseReference followerRef = databaseReference.child("following").child(userID);
                            followerRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for(DataSnapshot followerSnapShot : dataSnapshot.getChildren())
                                    {
                                        String followerUserId = followerSnapShot.getKey();

                                        DatabaseReference userRef =  databaseReference.child("Users").child(followerUserId);
                                        userRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists())
                                                {
                                                    String username = dataSnapshot.child("name").getValue(String.class);
                                                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                                                    Model model = new Model(imageUrl,username);
                                                    list.add(model);

                                                    viewUserActivityData.notifyDataSetChanged();
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
                        }
                        else
                        {
                            list.clear();
                            DatabaseReference followerRef = databaseReference.child("following").child(userID);
                            followerRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for(DataSnapshot followerSnapShot : dataSnapshot.getChildren())
                                    {
                                        String followerUserId = followerSnapShot.getKey();

                                        DatabaseReference userRef =  databaseReference.child("Users").child(followerUserId);
                                        userRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists())
                                                {
                                                    String username = dataSnapshot.child("name").getValue(String.class);
                                                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                                                    if(username.toLowerCase().startsWith(temp))
                                                    {
                                                        Model model = new Model(imageUrl,username);
                                                        list.add(model);
                                                    }

                                                    viewUserActivityData.notifyDataSetChanged();
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

                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            }


        }

    }
}