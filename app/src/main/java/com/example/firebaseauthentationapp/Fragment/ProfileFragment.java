package com.example.firebaseauthentationapp.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseauthentationapp.LoginActivity;
import com.example.firebaseauthentationapp.MainActivity;
import com.example.firebaseauthentationapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executor;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    AppCompatButton logout, upload, googleSignOut, txtProfileInfo;
    FirebaseAuth mAuth;
    // for data retrive
    FirebaseFirestore fStore;
    String userID;

    TextView user_email, user_name;
    StorageReference storageReference;

    FloatingActionButton pick_btn;
    CircleImageView profile;
    ProgressBar loading_progress;
    private Uri imageUri;
// for pick image after depricated of onActivityResult
    ActivityResultLauncher<String> activityResultLauncher;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        logout = view.findViewById(R.id.logout);
        user_email = view.findViewById(R.id.user_email);
        user_name = view.findViewById(R.id.user_name);
        pick_btn = view.findViewById(R.id.pick_btn);
        profile = view.findViewById(R.id.profile);
        loading_progress = view.findViewById(R.id.loading_progress);
        upload = view.findViewById(R.id.upload);
        googleSignOut = view.findViewById(R.id.googleSignOut);
        txtProfileInfo = view.findViewById(R.id.txtProfileInfo);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                // these two for google signout
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("676197636550-22u16ibtblbkqshacmca9gaosmu0tcde.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        mAuth = FirebaseAuth.getInstance();

//        googleSignOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Toast.makeText(getContext(), "Signed Out", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(getContext(), MainActivity.class));
//            }
//        });
// save google signIn account details
        txtProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();

                if(account != null){
                    String PersonName = account.getDisplayName();
                    String PersonEmail = account.getEmail();
                    Uri PersonPhoto = account.getPhotoUrl();

                    String profileInfo = "Name : " + PersonName + "\n" + "Email : " + "\n" + PersonEmail + "Photo : " + "\n" + PersonPhoto;

                    user_email.setText(PersonEmail);
                    user_name.setText(PersonName);
                    profile.setImageURI(PersonPhoto);
//            txtProfileInfo.setText(profileInfo);
                }
            }
        });


// retrieve data from firebase
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                user_email.setText(value.getString("email"));
                user_name.setText(value.getString("name"));

            }
        });

// store profile image
        storageReference= FirebaseStorage.getInstance().getReference();

        StorageReference profileRef =storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.img");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profile);
            }
        });

//        pick_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                upload.setVisibility(View.VISIBLE);
//
//                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(galleryIntent , 2);
//
//            }
//        });


// start activityfor result is depricated so new one is the below so first pick image by click pick_btn after set result in imageUri
//        for this no startActivityForReslt required  by this you can pick image from file

        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                profile.setImageURI(result);

                upload.setVisibility(View.VISIBLE);
                imageUri = result;
            }
        });

        pick_btn.setOnClickListener(vi-> launcher.launch("image/*"));

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageUri != null){
                    uploadToFirebase(imageUri);
                }else{
                    Toast.makeText(getContext(), "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode ==2 && resultCode == RESULT_OK ){
//
//            imageUri = data.getData();
//
//
//
//        }
//    }

    private void uploadToFirebase(Uri imageUri) {


        final StorageReference fileRef=storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.img");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profile);
                        loading_progress.setVisibility(View.INVISIBLE);

                        Toast.makeText(getContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                loading_progress.setVisibility(View.VISIBLE);
                upload.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading_progress.setVisibility(View.INVISIBLE);
                upload.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Uploading Failed !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}