package com.example.sessionexample.help;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sessionexample.MainActivity;
import com.example.sessionexample.R;
import com.example.sessionexample.Register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangeUsername extends AppCompatActivity {
    TextInputLayout NewUsername;
    EditText newUsername;
    ProgressBar UsernameProgress;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    String myCurrentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();

        NewUsername = findViewById(R.id.NewUsername);
        newUsername = NewUsername.getEditText();
        UsernameProgress = findViewById(R.id.UsernameProgress);

    }

    interface OnUsernameCheckListener
    {
        void onUsernameCheck(String myCurrentUsername);
    }

    private void getCurrentUsername(final OnUsernameCheckListener listener)
    {
        DatabaseReference currentUsernameRef = databaseReference.child("Users").child(mAuth.getUid());
        currentUsernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String username = dataSnapshot.child("name").getValue(String.class);
                    listener.onUsernameCheck(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("NotConstructor")
    public void ChangeUsername(View view) {

        UsernameProgress.setVisibility(View.VISIBLE);
        String Username = newUsername.getText().toString();

        DatabaseReference usersRef = databaseReference.child("Users");
        usersRef.orderByChild("name").equalTo(Username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    getCurrentUsername(new OnUsernameCheckListener() {
                        @Override
                        public void onUsernameCheck(String myCurrentUsername) {
                            if(myCurrentUsername.equals(Username))
                            {
                                updateNewUsername(Username);
                            }
                            else if(Username.isEmpty() || Username.trim().isEmpty() || Username.contains(" "))
                            {
                                Toast.makeText(ChangeUsername.this, "Invalid Username", Toast.LENGTH_LONG).show();
                                newUsername.setText("");
                                UsernameProgress.setVisibility(View.GONE);
                            }
                            else
                            {
                                Toast.makeText(ChangeUsername.this, "Username is already Registered!", Toast.LENGTH_LONG).show();
                                newUsername.setText("");
                                UsernameProgress.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else if(Username.isEmpty() || Username.trim().isEmpty() || Username.contains(" "))
                {
                    Toast.makeText(ChangeUsername.this, "Invalid Username", Toast.LENGTH_LONG).show();
                    newUsername.setText("");
                    UsernameProgress.setVisibility(View.GONE);
                }
                else
                {
                    updateNewUsername(Username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                UsernameProgress.setVisibility(View.GONE);
            }
        });

    }

    private void updateNewUsername(String newUsername)
    {
        DatabaseReference newUsernameRef = databaseReference.child("Users").child(mAuth.getUid());
        newUsernameRef.child("name").setValue(newUsername).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                UsernameProgress.setVisibility(View.GONE);
                Toast.makeText(ChangeUsername.this,"Username Changed!",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ChangeUsername.this, MainActivity.class);
                i.putExtra("moveToMyProfile","moveToMyProfile");
                startActivity(i);
                finish();
            }
        });
    }
}