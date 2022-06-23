package com.example.firebaseauthentationapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseauthentationapp.R;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseUser;

public class googleActivity extends AppCompatActivity {

    private SignInButton signInButton;
    private AppCompatButton btnSignOut, btnGetProfileInfo;
    private TextView txtProfileInfo;
    private GoogleSignInManager googleSignInManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);

        signInButton = findViewById(R.id.btnGoogleSignIn);
        btnSignOut = findViewById(R.id.btnSignOut);
        btnGetProfileInfo = findViewById(R.id.btnGetProfileInfo);
        txtProfileInfo = findViewById(R.id.txtProfileInfo);

        googleSignInManager = GoogleSignInManager.getInstance(this);
        googleSignInManager.setUpGoogleSignInOption();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInManager.signIn();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInManager.signOut();
                txtProfileInfo.setText("");
            }
        });

        btnGetProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser account = googleSignInManager.getProfileInfo();

                if(account != null){
                    String PersonName = account.getDisplayName();
                    String PersonEmail = account.getEmail();
                    Uri PersonPhoto = account.getPhotoUrl();

                    String profileInfo = "Name : " + PersonName + "\n" + "Email : " + "\n" + PersonEmail + "Photo : " + "\n" + PersonPhoto;

                    txtProfileInfo.setText(profileInfo);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(googleSignInManager.isUserAlreadySignIn()){
            Toast.makeText(this, "Already Signed In.", Toast.LENGTH_SHORT).show();
        }else {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == googleSignInManager.GOOGLE_SIGN_IN){
            googleSignInManager.handleSignInResult(data);
        }
    }
}