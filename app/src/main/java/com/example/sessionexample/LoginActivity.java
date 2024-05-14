package com.example.sessionexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText username,password;
    String UserEmail,UserPassword;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ProgressBar progressBar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        progressBar3 = findViewById(R.id.progressBar3);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            moveToMainActivity();
        }
        
    }


    private void moveToMainActivity() {
        progressBar3.setVisibility(View.GONE);
        Intent i = new Intent(LoginActivity.this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void register(View view) {

        moveToRegisterActivity();
    }

    private void moveToRegisterActivity() {
        progressBar3.setVisibility(View.GONE);

        Intent i = new Intent(LoginActivity.this,Register.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void forgotPassword(View view) {
        progressBar3.setVisibility(View.GONE);
        //Toast.makeText(LoginActivity.this, "Forgot Password", Toast.LENGTH_LONG).show();
        Intent i = new Intent(LoginActivity.this,VerifyUsername.class);
        startActivity(i);
    }

    public void login(View view) {
        progressBar3.setVisibility(View.VISIBLE);

        String Username,Password;
        Username = username.getText().toString();
        Password = password.getText().toString();

        if(Username.isEmpty() || Username.trim().isEmpty())
        {
            Toast.makeText(this,"Invalid Username Format",Toast.LENGTH_LONG).show();
        }
        else if(Password.isEmpty() || Password.trim().isEmpty())
        {
            Toast.makeText(this,"Invalid Password Format",Toast.LENGTH_LONG).show();
        }
        else
        {
            DatabaseReference usersRef = databaseReference.child("Users");
            usersRef.orderByChild("name").equalTo(Username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                            UserEmail = userSnapshot.child("email").getValue(String.class);
                            mAuth.signInWithEmailAndPassword(UserEmail, Password)
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                                moveToMainActivity();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Toast.makeText(LoginActivity.this, "Invalid Login Credentials", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    } else {
                        progressBar3.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Invalid Login Credentials", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar3.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Login Error", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}