package com.example.sessionexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    private EditText email,username,setPassword,confirmPassword;
    ProgressBar RegisterProgress;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();

        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        setPassword = findViewById(R.id.setPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        RegisterProgress = findViewById(R.id.RegisterProgress);
    }

    private int checkPassword() {

        String pwd1 = setPassword.getText().toString();
        String pwd2 = confirmPassword.getText().toString();

        if(!pwd1.equals(pwd2))
            return 0;
        else
            return 1;
    }

    private boolean isPasswordValid(String password)
    {
        String uppercaseRegex = ".*[A-Z].*";
        String symbolRegex = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*";
        String digitRegex = ".*\\d.*";

        boolean hasUppercase = password.matches(uppercaseRegex);
        boolean hasSymbol = password.matches(symbolRegex);
        boolean hasDigit = password.matches(digitRegex);

        return hasUppercase && hasSymbol && hasDigit;
    }

    private boolean checkPasswordLength(String password)
    {
        if(password.length() < 8)
            return false;
        else
            return true;
    }

    public void addUser(View view) {

        String Username,Password,Email;

        Email = email.getText().toString();
        Username = username.getText().toString();
        Password = setPassword.getText().toString();


        if(Email.isEmpty() || Email.trim().isEmpty())
        {
            Toast.makeText(this,"Invalid Email",Toast.LENGTH_LONG).show();
            email.setText("");
        }
        else if(Username.isEmpty() || Username.trim().isEmpty())
        {
            Toast.makeText(this,"Invalid Username",Toast.LENGTH_LONG).show();
            username.setText("");
        }
        else if(Username.contains(" "))
        {
            Toast.makeText(this,"Username should not contain Space character",Toast.LENGTH_LONG).show();
            username.setText("");
        }
        else if(Password.isEmpty() || Password.trim().isEmpty())
        {
            Toast.makeText(this,"Invalid Password Format",Toast.LENGTH_LONG).show();
            setPassword.setText("");
            confirmPassword.setText("");
        }
        else if(checkPassword() == 0)
        {
            Toast.makeText(this,"Password does not match",Toast.LENGTH_LONG).show();
            setPassword.setText("");
            confirmPassword.setText("");
        }
        else if(!isPasswordValid(Password))
        {
            Toast.makeText(this,"Password must contains Atleast 1 UpperCase character,1 symbol,1 digit",Toast.LENGTH_LONG).show();
            setPassword.setText("");
            confirmPassword.setText("");
        }
        else if(!checkPasswordLength(Password))
        {
            Toast.makeText(this,"Password must be 8 characters long",Toast.LENGTH_LONG).show();
            setPassword.setText("");
            confirmPassword.setText("");
        }
        else
        {
            DatabaseReference usersRef = databaseReference.child("Users");
            usersRef.orderByChild("name").equalTo(Username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists())
                    {
                        Toast.makeText(Register.this,"Username is already Registered!",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        checkIfEmailRegistered(Username,Email,Password);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    private void checkIfEmailRegistered(String Username,String Email,String Password)
    {
        DatabaseReference usersRef = databaseReference.child("Users");
        usersRef.orderByChild("email").equalTo(Email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    Toast.makeText(Register.this,"Email is already Registered!",Toast.LENGTH_LONG).show();
                }
                else
                {
                    createUser(Username,Email,Password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void createUser(String Username,String Email,String Password)
    {
        RegisterProgress.setVisibility(View.VISIBLE);
        User newUser = new User();
        newUser.setName(Username);
        newUser.setEmail(Email);

        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            database.getReference().child("Users").child(userId).setValue(newUser);

                            Toast.makeText(Register.this,"Registration Successful",Toast.LENGTH_LONG).show();
                            Intent i = new Intent(Register.this,LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);

                        } else {
                            RegisterProgress.setVisibility(View.GONE);

                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}