package com.example.group3projectpetcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class Services extends AppCompatActivity {
    private Button servicesbackButton;
    private ImageView updatesImageView;
    private DatabaseReference updatesReference;
    private TextView DateAndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        servicesbackButton = findViewById(R.id.servicesbackButton);
        updatesImageView = findViewById(R.id.updatesImageView);
        DateAndTime = findViewById(R.id.DateAndTime);

        servicesbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserMainInterface.class);
                startActivity(intent);
                finish();
            }
        });

        updatesReference = FirebaseDatabase.getInstance().getReference().child("updates").child("update");
        updatesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("update")) {
                    String imageUrl = dataSnapshot.child("update").getValue(String.class);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Picasso.get().load(imageUrl).into(updatesImageView);
                    }
                }

                if (dataSnapshot.hasChild("timestamp")) {
                    long timestamp = dataSnapshot.child("timestamp").getValue(Long.class);
                    if (timestamp != 0) {
                        Date uploadDate = new Date(timestamp);
                        String formattedDate = DateFormat.getDateInstance().format(uploadDate);
                        String formattedTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(uploadDate);
                        String displayDateTime = "Date: " + formattedDate + "   -   " +"Time: " + formattedTime;
                        DateAndTime.setText(displayDateTime);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}
