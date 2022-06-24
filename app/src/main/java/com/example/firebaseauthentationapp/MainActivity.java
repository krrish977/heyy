package com.example.firebaseauthentationapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseauthentationapp.Fragment.ProfileFragment;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText name, email;
    ProgressBar progress;
    AppCompatButton sign_up;
    TextInputEditText pass, cnf_pass;
    TextView go_to_login;

    String user_name, password, confirm_password, user_email;
    // for get data & after get fetch in profile activity
    FirebaseFirestore fstore;
    String userID;

    SignInButton googleSignInButton;
    GoogleSignInClient mGoogleSignInClient;
    String TAG = "MainActivity";
    int RC_SIGN_IN = 1;

    LoginButton fb_login_button;
    LinearLayout btn_facebook;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        cnf_pass = findViewById(R.id.cnf_pass);
        progress = findViewById(R.id.progress);
        sign_up = findViewById(R.id.sign_up);
        go_to_login = findViewById(R.id.go_to_login);

        go_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });


        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progress.setVisibility(View.VISIBLE);

                user_name = name.getText().toString();
                user_email = email.getText().toString();
                password = pass.getText().toString();
                confirm_password = cnf_pass.getText().toString();

                if( user_name.isEmpty()){
                    name.setError("Please fill this field");
                    return;
                }
                if (user_email.isEmpty()){
                    email.setError("Email required");
                    return;
                }
                if (password.isEmpty()){
                    pass.setError("Password required");
                    return;
                }
                if(password.length()<6) {
                    pass.setError("password must be >= 6 character");
                    return;
                }
                if(confirm_password.isEmpty()){
                    cnf_pass.setError("Confirm your Password");
                    return;
                }
                if(!confirm_password.equals(password)){
                    cnf_pass.setError("Confirm Password must be same");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(user_email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(getApplicationContext(), "Registration successful..", Toast.LENGTH_SHORT).show();

  // this is for get user data & fetch in profile frag
                        userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fstore.collection("users").document(userID);
                        Map<String,Object> userX = new HashMap<>();
                        userX.put("name", user_name);
                        userX.put("email", user_email);

                        documentReference.set(userX).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG,"onSuccess"+ userID);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.d(TAG,"onFailure"+e.toString());
                            }
                        });

                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                        progress.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progress.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Googleinitializations();


    }

    private void Googleinitializations() {
        googleSignInButton = findViewById(R.id.sign_In_Button);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("676197636550-22u16ibtblbkqshacmca9gaosmu0tcde.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        // Check condition
        if(firebaseUser!=null)
        {
            // When user already sign in
            // redirect to profile activity
            startActivity(new Intent(MainActivity.this,googleActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
        Log.d(TAG, "begin Google Sign In");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // Check condition
    if(requestCode==RC_SIGN_IN)
    {
        // When request code is equal to 100
        // Initialize task
        Task<GoogleSignInAccount> signInAccountTask=GoogleSignIn
                .getSignedInAccountFromIntent(data);

        // check condition
        if(signInAccountTask.isSuccessful()) {
            // When google sign in successful
            // Initialize string
            String s="Google sign in successful";
            // Display Toast
            displayToast(s);
            // Initialize sign in account
            try {
                // Initialize sign in account
                GoogleSignInAccount googleSignInAccount=signInAccountTask
                        .getResult(ApiException.class);
                // Check condition
                if(googleSignInAccount!=null)
                {
                    // When sign in account is not equal to null
                    // Initialize auth credential
                    AuthCredential authCredential= GoogleAuthProvider
                            .getCredential(googleSignInAccount.getIdToken()
                                    ,null);
                    // Check credential
                    mAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // Check condition
                                    if(task.isSuccessful())
                                    {
                                        // When task is successful
                                        // Redirect to profile activity
                                        startActivity(new Intent(MainActivity.this
                                                ,MainActivity2.class)
                                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                        // Display Toast
                                        displayToast("Firebase authentication successful");
                                    }
                                    else
                                    {
                                        // When task is unsuccessful
                                        // Display Toast
                                        displayToast("Authentication Failed :"+task.getException()
                                                .getMessage());
                                    }
                                }
                            });

                }
            }
            catch (ApiException e)
            {
                e.printStackTrace();

            }
        }else {
            Log.d(TAG, "Error ...");
        }
    }
}

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            startActivity(new Intent(getApplicationContext(), MainActivity2.class));
            this.finish();
        }
    }

}