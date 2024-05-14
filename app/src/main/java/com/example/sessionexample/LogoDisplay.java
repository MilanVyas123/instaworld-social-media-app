package com.example.sessionexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LogoDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_display);

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                delayDisplayLogo();
            }
        };

        handler.postDelayed(runnable,3000);
    }

    private void delayDisplayLogo()
    {
        moveToLoginActivity();
    }

    private void moveToLoginActivity()
    {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser != null) {
            currentUser.getIdToken(true) // Force refresh the token
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            Intent i = new Intent(LogoDisplay.this,LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        } else {
                            Intent i = new Intent(LogoDisplay.this,LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    });
        } else {

            Intent i = new Intent(LogoDisplay.this,LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

    }
}