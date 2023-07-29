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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Appointments extends AppCompatActivity {
    private Button appointmentbackButton, historyButton;
    RecyclerView recyclerView;
    ArrayList<Schedule> list;
    DatabaseReference databaseReference;
    AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        recyclerView = findViewById(R.id.appointmentRecyclerView);
        databaseReference = FirebaseDatabase.getInstance().getReference("bookings");
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentAdapter(this, list);
        recyclerView.setAdapter(adapter);

        retrieveUserId(); // Call the method to retrieve user ID

        appointmentbackButton = findViewById(R.id.appointmentbackButton);
        historyButton = findViewById(R.id.historyButton);

        appointmentbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserMainInterface.class);
                startActivity(intent);
                finish();
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), User_History.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void retrieveUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            retrieveBookings(userId);
        }
    }

    private void retrieveBookings(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("bookings").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    list.clear(); // Clear the list before adding new data

                    // Get the current date
                    Calendar currentDate = Calendar.getInstance();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Schedule schedule = snapshot.getValue(Schedule.class);

                        // Get the booking date
                        String bookingDateString = schedule.getDate();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
                        try {
                            Date bookingDate = dateFormat.parse(bookingDateString);

                            // Check if the booking date is current or future
                            Calendar bookingCalendar = Calendar.getInstance();
                            bookingCalendar.setTime(bookingDate);

                            if (!bookingCalendar.before(currentDate)) {
                                list.add(schedule);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
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
