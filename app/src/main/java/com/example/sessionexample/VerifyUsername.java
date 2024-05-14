package com.example.sessionexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VerifyUsername extends AppCompatActivity {
    FirebaseDatabase database;

    DatabaseReference databaseReference;
    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_username);

        username = findViewById(R.id.username);

        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");

        databaseReference = database.getReference();
    }

    public void next(View view) {
        String Username;
        Username = username.getText().toString();
        if(Username.isEmpty() || Username.trim().isEmpty())
        {
            Toast.makeText(VerifyUsername.this,"Invalid Username Format",Toast.LENGTH_LONG).show();
            username.setText("");
        }
        else
        {
            DatabaseReference usersRef = databaseReference.child("Users");
            usersRef.orderByChild("name").equalTo(Username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String id = userSnapshot.getKey(); // This gets the "id" of the user
                            String Email = userSnapshot.child("email").getValue(String.class);
                            Intent i = new Intent(VerifyUsername.this, ForgotPassword.class);
                            i.putExtra("userId", id); // Pass the user's ID to the next activity
                            i.putExtra("email",Email);
                            startActivity(i);
                        }
                    }
                    else
                    {
                        Toast.makeText(VerifyUsername.this,"Invalid Username",Toast.LENGTH_LONG).show();
                        username.setText("");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(VerifyUsername.this, "Authentication failed", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}