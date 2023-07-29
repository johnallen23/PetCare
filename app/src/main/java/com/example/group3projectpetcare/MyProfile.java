package com.example.group3projectpetcare;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MyProfile extends AppCompatActivity {

    private TextView usernameTextview;
    private TextView emailTextview;
    private TextView locationTextview;
    private TextView numberTextview;
    private Button profilebackButton, logoutButton;
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        profilebackButton = findViewById(R.id.profilebackButton);
        logoutButton = findViewById(R.id.logoutButton);
        usernameTextview = findViewById(R.id.usernameTextview);
        emailTextview = findViewById(R.id.emailTextview);
        locationTextview = findViewById(R.id.locationTextview);
        numberTextview = findViewById(R.id.numberTextview);
        profileImageView = findViewById(R.id.profileImageView);

        profilebackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserMainInterface.class);
                startActivity(intent);
                finish();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MyProfile.this, "Logout successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeProfileImage.class);
                startActivity(intent);
                finish();
            }
        });


        retrieveUserInfo();
    }

    private void retrieveUserInfo() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            // Set the user information to the appropriate views
                            usernameTextview.setText(user.getUsername());
                            emailTextview.setText(user.getEmail());
                            locationTextview.setText(user.getLocation());
                            numberTextview.setText(user.getNumber());

                            String profileImage = user.getProfileImage();
                            if (!TextUtils.isEmpty(profileImage)) {
                                // Use a library like Picasso or Glide to load and display the image
                                Picasso.get().load(profileImage).into(profileImageView);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }

}
