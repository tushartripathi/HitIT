package com.vubird.hitit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.net.SocketAddress;

public class MainActivity extends AppCompatActivity {
    EditText usernameET, EmailET, PasswordET;
    private FirebaseAuth mAuth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        usernameET = findViewById(R.id.usernameBox);
        EmailET = findViewById(R.id.EmailBox);
        PasswordET = findViewById(R.id.passwordBox);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            gotoSocialMediaActivity();
        }
        else
        {
            Toast.makeText(this,"No user found", Toast.LENGTH_SHORT).show();
        }
    }


    public void SignUpUser(View view)
    {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Create Room..");
        dialog.show();

        mAuth.createUserWithEmailAndPassword(EmailET.getText().toString(), PasswordET.getText().toString())
                .addOnCompleteListener(this ,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                dialog.dismiss();
                if(task.isSuccessful())
                {
                    user = mAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(usernameET.getText().toString().trim()).build();
                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            Toast.makeText(MainActivity.this, "welocme " + user.getEmail(),Toast.LENGTH_SHORT).show();
                        }
                    });
//                    FirebaseDatabase.getInstance().getReference().child("my_users").child(task.getResult().getUser().getUid()).child("username").setValue(usernameET.getText().toString().trim());
//                    FirebaseUser user = mAuth.getCurrentUser();
                      gotoSocialMediaActivity();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Error ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void LogInUser(View view)
    {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Login..");
        dialog.show();
        mAuth.signInWithEmailAndPassword(EmailET.getText().toString(), PasswordET.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                dialog.dismiss();

                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "SignIn" ,Toast.LENGTH_SHORT).show();
                            gotoSocialMediaActivity();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Error" ,Toast.LENGTH_SHORT).show();
                        }
            }
        });

    }

    private void gotoSocialMediaActivity()
    {

        Intent i = new Intent(this,SocialMediaAcitcity.class);
        startActivity(i);

    }


}
