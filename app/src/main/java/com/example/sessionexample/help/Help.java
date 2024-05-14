package com.example.sessionexample.help;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.sessionexample.R;
import com.example.sessionexample.Register;

public class Help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }

    public void DeleteAccount(View view) {
        Intent i = new Intent(this, DeleteAccount.class);
        startActivity(i);
    }

    public void CreateAccount(View view) {
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }

    public void ChangeUsername(View view) {
        Intent i = new Intent(this, ChangeUsername.class);
        startActivity(i);
    }

    public void ChangePassword(View view) {
        Intent i = new Intent(this, ChangePassword.class);
        startActivity(i);
    }
}