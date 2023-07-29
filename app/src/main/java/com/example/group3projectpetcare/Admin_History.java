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

public class Admin_History extends AppCompatActivity {

    private Button adminbackButton;
    RecyclerView recyclerView;
    ArrayList<Schedule> list;
    DatabaseReference databaseReference;
    AdminAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_history);

        recyclerView = findViewById(R.id.appointmentRecyclerView);
        databaseReference = FirebaseDatabase.getInstance().getReference("bookings");
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminAdapter(this, list);
        recyclerView.setAdapter(adapter);

        adminbackButton = findViewById(R.id.adminbackButton);

        adminbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScheduleView.class);
                startActivity(intent);
                finish();
            }
        });

        retrieveAllBookings();
    }

    private void retrieveAllBookings() {
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
        bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    list.clear(); // Clear the list before adding new data

                    // Get the current date
                    Calendar currentDate = Calendar.getInstance();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey(); // Retrieve the user ID
                        for (DataSnapshot bookingSnapshot : userSnapshot.getChildren()) {
                            String bookingId = bookingSnapshot.getKey(); // Retrieve the booking ID
                            Schedule schedule = bookingSnapshot.getValue(Schedule.class);

                            // Get the booking date
                            String bookingDateString = schedule.getDate();
                            Date bookingDate;
                            try {
                                bookingDate = dateFormat.parse(bookingDateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                continue; // Skip this booking if date parsing fails
                            }

                            // Check if the booking date has already passed
                            Calendar bookingCalendar = Calendar.getInstance();
                            bookingCalendar.setTime(bookingDate);

                            if (bookingCalendar.before(currentDate)) {
                                schedule.setUserId(userId); // Set the user ID for the schedule
                                schedule.setBookingId(bookingId); // Set the booking ID for the schedule
                                list.add(schedule);
                            }
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
