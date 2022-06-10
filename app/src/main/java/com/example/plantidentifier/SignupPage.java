package com.example.plantidentifier;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignupPage extends AppCompatActivity {

    EditText email;
    EditText password;
    Button signupBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        mAuth = FirebaseAuth.getInstance();
        email = (EditText)findViewById(R.id.signupEmailField);
        password = (EditText)findViewById(R.id.signupPasswordField);
        signupBtn = (Button)findViewById(R.id.signupButtonAction);

        signupBtn.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View view) {
                createUser();
            }
        });


    }

    public void createUser(){
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if(TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)){
            Toast.makeText(SignupPage.this,"Please fill all fields",Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String id = mAuth.getCurrentUser().getUid();
                        ArrayList<Object> array = new ArrayList<>();
                        Map<String, Object> docData = new HashMap<>();

                        docData.put("flowers", array);
                        Toast.makeText(SignupPage.this,"User registered successfully",Toast.LENGTH_LONG).show();
                        db.collection("users").document(id).set(docData);
                        startActivity(new Intent(SignupPage.this, HomePage.class));

                    } else {
                        Toast.makeText(SignupPage.this,"ERROR: " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

}