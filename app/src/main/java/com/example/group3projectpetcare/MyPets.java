package com.example.group3projectpetcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MyPets extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<NewPet> list;
    DatabaseReference databaseReference;
    PetAdapter adapter;
    private Button petsbackButton, addpetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pets);

        recyclerView = findViewById(R.id.petsRecyclerView);
        databaseReference = FirebaseDatabase.getInstance().getReference("MyPets");
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PetAdapter(this, list);
        recyclerView.setAdapter(adapter);

        retrieveUserInfo(); // Call the method to retrieve user info

        petsbackButton = findViewById(R.id.petsbackButton);
        addpetButton = findViewById(R.id.addpetButton);

        petsbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserMainInterface.class);
                startActivity(intent);
                finish();
            }
        });

        addpetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddPet.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void retrieveUserInfo() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("MyPets").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        list.clear(); // Clear the list before adding new data
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            NewPet newPet = snapshot.getValue(NewPet.class);
                            list.add(newPet);
                        }
                        adapter.notifyDataSetChanged(); // Notify the adapter about the data change
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

