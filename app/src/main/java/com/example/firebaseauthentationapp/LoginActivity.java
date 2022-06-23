package com.example.firebaseauthentationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText login_email;
    TextInputEditText login_pass;
    AppCompatButton log_in;
    TextView go_to_register;
    ProgressBar login_progress;

    String email, password;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        login_email = findViewById(R.id.login_email);
        login_pass = findViewById(R.id.login_pass);
        log_in = findViewById(R.id.log_in);
        go_to_register = findViewById(R.id.go_to_register);
        login_progress = findViewById(R.id.login_progress);

        go_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        });

        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_progress.setVisibility(View.VISIBLE);

                email = login_email.getText().toString();
                password = login_pass.getText().toString();

                if (email.isEmpty()){
                    login_email.setError("Email required");
                }
                if (password.isEmpty()){
                    login_pass.setError("Password required");
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this,MainActivity2.class);
                        startActivity(intent);
                        login_progress.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        login_progress.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() !=null) {
            startActivity(new Intent(getApplicationContext(), MainActivity2.class));

        }

    }
}