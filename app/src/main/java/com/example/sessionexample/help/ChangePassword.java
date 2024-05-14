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

import com.example.sessionexample.R;
import com.example.sessionexample.VerifyUsername;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePassword extends AppCompatActivity {
    TextInputLayout CurrentPassword,NewPassword,ReTypePassword;
    EditText currentPassword,newPassword,reTypePassword;
    ProgressBar PasswordProgress;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();

        CurrentPassword = findViewById(R.id.CurrentPassword);
        NewPassword = findViewById(R.id.NewPassword);
        ReTypePassword = findViewById(R.id.ReTypePassword);
        PasswordProgress = findViewById(R.id.PasswordProgress);

        currentPassword = CurrentPassword.getEditText();
        newPassword = NewPassword.getEditText();
        reTypePassword = ReTypePassword.getEditText();
    }

    interface OnPasswordCheckListener
    {
        void onPasswordCheck(boolean isCorrect);
    }
    private void checkCurrentPassword(final OnPasswordCheckListener listener)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String pwd = currentPassword.getText().toString();
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), pwd);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    listener.onPasswordCheck(true);
                }
                else
                {
                    listener.onPasswordCheck(false);
                }
            }
        });
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

    @SuppressLint("NotConstructor")
    public void ChangePassword(View view) {

        PasswordProgress.setVisibility(View.VISIBLE);
        String newPwd = newPassword.getText().toString();
        String reTypePwd = reTypePassword.getText().toString();

        checkCurrentPassword(new OnPasswordCheckListener() {
            @Override
            public void onPasswordCheck(boolean isCorrect) {
                if(isCorrect)
                {
                    if(newPwd.equals(reTypePwd))
                    {
                        if(newPwd.length() < 8)
                        {
                            PasswordProgress.setVisibility(View.GONE);
                            Toast.makeText(ChangePassword.this,"Password must be atleast 8 characters long!",Toast.LENGTH_LONG).show();
                            newPassword.setText("");
                            reTypePassword.setText("");
                        }
                        else if(isPasswordValid(newPwd))
                        {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updatePassword(newPwd)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                PasswordProgress.setVisibility(View.GONE);
                                                Toast.makeText(ChangePassword.this,"Password Changed!",Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(ChangePassword.this, Help.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            PasswordProgress.setVisibility(View.GONE);
                            Toast.makeText(ChangePassword.this,"Invalid Password!",Toast.LENGTH_LONG).show();
                            newPassword.setText("");
                            reTypePassword.setText("");
                        }

                    }
                    else
                    {
                        PasswordProgress.setVisibility(View.GONE);
                        Toast.makeText(ChangePassword.this,"Password Does Not Match!",Toast.LENGTH_LONG).show();
                        newPassword.setText("");
                        reTypePassword.setText("");
                    }
                }
                else
                {
                    PasswordProgress.setVisibility(View.GONE);
                    Toast.makeText(ChangePassword.this,"Current Password is Wrong!",Toast.LENGTH_LONG).show();
                    currentPassword.setText("");
                }
            }
        });

    }

    public void ForgotPassword(View view) {
        Intent i = new Intent(this, VerifyUsername.class);
        startActivity(i);
    }
}