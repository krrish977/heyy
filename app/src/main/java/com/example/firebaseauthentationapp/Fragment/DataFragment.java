package com.example.firebaseauthentationapp.Fragment;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.firebaseauthentationapp.MainActivity;
import com.example.firebaseauthentationapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class DataFragment extends Fragment {

    FirebaseAuth mAuth;
    // for data retrive
    FirebaseFirestore fStore;
    String userID;

    TextView user_name, text_about, main_text_about;
    EditText d_about;
    AppCompatButton save, edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data, container, false);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        user_name = view.findViewById(R.id.d_name);
        d_about = view.findViewById(R.id.d_about);
        text_about = view.findViewById(R.id.text_about);
        save = view.findViewById(R.id.save);
        edit = view.findViewById(R.id.edit);
        main_text_about = view.findViewById(R.id.main_text_about);

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                user_name.setText(value.getString("name"));

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d_about.setVisibility(View.GONE);
                text_about.setVisibility(View.VISIBLE);
                save.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);

                String content = d_about.getText().toString();
                text_about.setText(" " + content);

            }
        });
        d_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d_about.setVisibility(View.VISIBLE);
//                save.setVisibility(View.VISIBLE);
                text_about.setVisibility(View.GONE);


            }
        });
        if(d_about.getText().toString() != null){
            save.setVisibility(View.VISIBLE);
        }
        text_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setVisibility(View.GONE);
                d_about.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                text_about.setVisibility(View.GONE);
            }
        });

        return view;
    }

}