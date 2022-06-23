package com.example.firebaseauthentationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class googleSignOutActivity extends AppCompatActivity {

    CircleImageView ivImage;
    AppCompatButton btLogout;
    TextView tvName;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_out);

        ivImage=findViewById(R.id.iv_image);
        tvName=findViewById(R.id.tv_name);
        btLogout=findViewById(R.id.bt_logout);

        // Initialize firebase auth
        firebaseAuth=FirebaseAuth.getInstance();

        // Initialize firebase user
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

        // Check condition
        if(firebaseUser!=null)
        {
            // When firebase user is not equal to null
            // Set image on image view
            Picasso.get()
                    .load(firebaseUser.getPhotoUrl())
                    .into(ivImage);
            // set name on text view
            tvName.setText(firebaseUser.getDisplayName());
        }

        // Initialize sign in client
        googleSignInClient= GoogleSignIn.getClient(googleSignOutActivity.this
                , GoogleSignInOptions.DEFAULT_SIGN_IN);

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign out from google
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Check condition
                        if(task.isSuccessful())
                        {
                            // When task is successful
                            // Sign out from firebase
                            firebaseAuth.signOut();

                            // Display Toast
                            Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();

                            // Finish activity
                            finish();
                        }
                    }
                });
            }
        });
    }
}