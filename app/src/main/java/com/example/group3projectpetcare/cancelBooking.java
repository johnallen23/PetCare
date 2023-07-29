package com.example.group3projectpetcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class cancelBooking extends AppCompatActivity {
    private Button confirmButton, cancelbackButton;
    private Spinner reasonSpinner;
    private EditText reasonEditText;
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference bookingRef;
    private String userId, bookingId;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_booking);

        context = cancelBooking.this;

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(context, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
        }

        confirmButton = findViewById(R.id.confirmButton);
        cancelbackButton = findViewById(R.id.cancelbackButton);
        reasonEditText = findViewById(R.id.reasonEditText);
        reasonSpinner = findViewById(R.id.reasonSpinner);

        // Set up spinner
        String[] reasonOptions = {
                "--Select--",
                "I am sick or feeling unwell.",
                "I might reconsider rescheduling for another day.",
                "I have a personal or family emergency.",
                "I am in another meeting or appointment.",
                "The appointment is no longer beneficial for me.",
                "I have other reason/s."
        };

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reasonOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reasonSpinner.setAdapter(spinnerAdapter);

        cancelbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Appointments.class);
                startActivity(intent);
                finish();
            }
        });

        reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedReason = parent.getItemAtPosition(position).toString();

                if (selectedReason.equals("--Select--")) {
                    reasonEditText.setVisibility(View.GONE);
                } else if (selectedReason.equals("I have other reason/s.")) {
                    reasonEditText.setVisibility(View.VISIBLE);
                } else {
                    reasonEditText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedReason = reasonSpinner.getSelectedItem().toString();
                String reason;

                if (selectedReason.equals("--Select--")) {
                    Toast.makeText(context, "Select or state your reasons for cancelling.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (selectedReason.equals("I have other reason/s.")) {
                    reason = reasonEditText.getText().toString().trim();

                    if (reason.isEmpty()) {
                        Toast.makeText(context, "Please provide a cancellation reason.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    reason = selectedReason;
                }

                DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings")
                        .child(userId);

                bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                                bookingId = bookingSnapshot.getKey();
                                updateBookingStatus(reason);
                            }
                        } else {
                            Toast.makeText(context, "No bookings found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(context, "Failed to retrieve bookings.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void updateBookingStatus(String reason) {
        bookingRef = FirebaseDatabase.getInstance().getReference("bookings")
                .child(userId)
                .child(bookingId);

        bookingRef.child("status").setValue("Waiting for Approval");
        bookingRef.child("reason").setValue(reason);

        Toast.makeText(context, "Your Cancellation request is sent to our admins.", Toast.LENGTH_LONG).show();
        Toast.makeText(context, "Always check for status updates.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, Appointments.class);
        context.startActivity(intent);
    }
}
