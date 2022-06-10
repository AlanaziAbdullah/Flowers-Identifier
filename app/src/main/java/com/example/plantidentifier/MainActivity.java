package com.example.plantidentifier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button signInBtn;
    Button signUpBtn;

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(MainActivity.this, HomePage.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        signInBtn = (Button)findViewById(R.id.signInButton);
        signUpBtn = (Button)findViewById(R.id.signUpButton);
        auth = FirebaseAuth.getInstance();

        signInBtn.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openSignInPage();
            }
        });
        signUpBtn.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openSignupPage();
            }
        });

    }

    void openSignInPage(){
        Intent intent = new Intent(this, SigninPage.class);
        startActivity(intent);
    }

    void openSignupPage(){
        Intent intent = new Intent(this, SignupPage.class);
        startActivity(intent);
    }
}