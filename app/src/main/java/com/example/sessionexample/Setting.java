package com.example.sessionexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sessionexample.help.Help;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        DatabaseReference databaseReference = database.getReference();
        String currentUserID = mAuth.getUid();

        Switch privateAccount = findViewById(R.id.switch2);

        DatabaseReference checkPrivateAccount = database.getReference("Users").child(currentUserID).child("status");
        checkPrivateAccount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String status = dataSnapshot.getValue(String.class);
                    if(status.equals("public"))
                    {
                        privateAccount.setChecked(false);
                    }
                    else
                    {
                        privateAccount.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String username = getIntent().getStringExtra("username");

        TextView logout = findViewById(R.id.logout);
        logout.setText(logout.getText().toString() + " " + username);


        privateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(privateAccount.isChecked())
                {
                    // make account private?

                    AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
                    builder.setTitle("Are you Sure?");
                    TextView message = new TextView(Setting.this);
                    message.setText("\n" + "Switch to private");
                    message.setTextColor(Color.BLUE);
                    message.setTextSize(20);
                    message.setTypeface(null, Typeface.BOLD);
                    message.setGravity(Gravity.CENTER);
                    builder.setView(message);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // make account private? Yes

                            DatabaseReference switchToPrivate = database.getReference("Users").child(currentUserID).child("status");
                            switchToPrivate.setValue("private");

                            Toast.makeText(Setting.this,"Switched to private",Toast.LENGTH_LONG).show();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            privateAccount.setChecked(false);
                            dialog.dismiss();
                        }
                    });

                    builder.show();

                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
                    builder.setTitle("Are you Sure?");
                    TextView message = new TextView(Setting.this);
                    message.setText("\n" + "Switch to public");
                    message.setTextColor(Color.BLUE);
                    message.setTextSize(20);
                    message.setTypeface(null, Typeface.BOLD);
                    message.setGravity(Gravity.CENTER);
                    builder.setView(message);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // make account public? Yes

                            DatabaseReference switchToPrivate = database.getReference("Users").child(currentUserID).child("status");
                            switchToPrivate.setValue("public");

                            Toast.makeText(Setting.this,"Switched to public",Toast.LENGTH_LONG).show();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            privateAccount.setChecked(true);
                        }
                    });

                    builder.show();
                }
            }
        });



    }

    public void register(View view) {
        Intent i = new Intent(Setting.this,Register.class);
        startActivity(i);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(Setting.this,"Logged Out",Toast.LENGTH_SHORT).show();
        moveToLoginActivity();
    }

    private void moveToLoginActivity() {
        Intent i = new Intent(Setting.this,LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void AboutUs(View view) {
        Intent i = new Intent(Setting.this,AboutUs.class);
        startActivity(i);
    }

    public void help(View view) {
        Intent i = new Intent(Setting.this, Help.class);
        startActivity(i);
    }
}