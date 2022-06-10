package com.example.plantidentifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SigninPage extends AppCompatActivity {

    EditText email;
    EditText password;
    Button signinBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_page);


        mAuth = FirebaseAuth.getInstance();
        email = (EditText)findViewById(R.id.signInEmailField);
        password = (EditText)findViewById(R.id.signInPasswordField);
        signinBtn = (Button)findViewById(R.id.signInButtonAction);

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
            }
        });

    }

    public void signInUser(){

        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if(TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)){
            Toast.makeText(SigninPage.this,"Please fill all fields",Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        startActivity(new Intent(SigninPage.this, HomePage.class));
                    }else {
                        Toast.makeText(SigninPage.this,"ERROR: " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }



    }
}