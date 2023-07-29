package com.example.group3projectpetcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Locale;

public class ScheduleView extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private RecyclerView recyclerView;
    private ArrayList<Schedule> list;
    private DatabaseReference databaseReference;
    private AdminAdapter adapter;
    private Button schedbackButton, datePickerButton, historyButton;
    private RadioButton MonthradioButton, DateradioButton;
    private Spinner monthSpinner;
    private TextView filterTextView;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_view);

        recyclerView = findViewById(R.id.adminRecyclerView);
        databaseReference = FirebaseDatabase.getInstance().getReference("bookings");
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminAdapter(this, list);
        recyclerView.setAdapter(adapter);

        schedbackButton = findViewById(R.id.schedbackButton);
        historyButton = findViewById(R.id.historyButton);

        MonthradioButton = findViewById(R.id.MonthradioButton);
        DateradioButton = findViewById(R.id.DateradioButton);

        monthSpinner = findViewById(R.id.monthSpinner);
        datePickerButton = findViewById(R.id.datePickerButton);

        filterTextView = findViewById(R.id.filterTextView);


        // Initialize spinner adapter
        List<String> months = new ArrayList<>();
        months.add("All");
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        monthSpinner.setAdapter(spinnerAdapter);
        monthSpinner.setOnItemSelectedListener(this);

        monthSpinner.setVisibility(View.GONE);
        datePickerButton.setVisibility(View.GONE);

        MonthradioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthSpinner.setVisibility(View.VISIBLE);
                datePickerButton.setVisibility(View.GONE);
                updateFilterTextViewNoSelection();
            }
        });

        DateradioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthSpinner.setVisibility(View.GONE);
                datePickerButton.setVisibility(View.VISIBLE);
                updateFilterTextViewNoSelection();
                retrieveBookingsByDate(""); // Pass an empty string initially
            }
        });


        schedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdminMainInterface.class);
                startActivity(intent);
                finish();
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Admin_History.class);
                startActivity(intent);
                finish();
            }
        });

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDate();
            }
        });


        retrieveAllBookings();
        updateFilterTextView("All bookings");
    }

    private void openDate() {
        // Get the current date from the Calendar
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleView.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Retrieve bookings for the selected date
                String formattedDate = String.format(Locale.US, "%02d-%02d-%04d", monthOfYear + 1, dayOfMonth, year);
                retrieveBookingsByDate(formattedDate);
            }
        }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void updateFilterTextView(String filterText) {
        filterTextView.setText(filterText);
    }

    private void updateFilterTextViewNoSelection() {
        filterTextView.setText("No Selected Month or Date");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedMonth = parent.getItemAtPosition(position).toString();
        if (selectedMonth.equals("All")) {
            retrieveAllBookings();
            updateFilterTextView("All bookings");
        } else {
            retrieveBookingsByMonth(selectedMonth);
            updateFilterTextView("Month of " + selectedMonth);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        retrieveAllBookings();
        updateFilterTextViewNoSelection();
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

                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey(); // Retrieve the user ID
                        for (DataSnapshot bookingSnapshot : userSnapshot.getChildren()) {
                            String bookingId = bookingSnapshot.getKey(); // Retrieve the booking ID
                            Schedule schedule = bookingSnapshot.getValue(Schedule.class);

                            // Get the booking date
                            String bookingDateString = schedule.getDate();
                            Date bookingDate;
                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
                                bookingDate = dateFormat.parse(bookingDateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                continue; // Skip this booking if date parsing fails
                            }

                            // Check if the booking date has already occurred
                            Calendar bookingCalendar = Calendar.getInstance();
                            bookingCalendar.setTime(bookingDate);

                            if (!bookingCalendar.before(currentDate)) {
                                schedule.setUserId(userId); // Set the user ID for the schedule
                                schedule.setBookingId(bookingId); // Set the booking ID for the schedule
                                list.add(schedule);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged(); // Notify the adapter about the data change
                }
                if (list.isEmpty()) {
                    updateFilterTextView("No bookings available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }


    private void retrieveBookingsByMonth(final String selectedMonth) {
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
        bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    list.clear(); // Clear the list before adding new data
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey(); // Retrieve the user ID
                        for (DataSnapshot bookingSnapshot : userSnapshot.getChildren()) {
                            String bookingId = bookingSnapshot.getKey(); // Retrieve the booking ID
                            Schedule schedule = bookingSnapshot.getValue(Schedule.class);
                            schedule.setUserId(userId); // Set the user ID for the schedule
                            schedule.setBookingId(bookingId); // Set the booking ID for the schedule

                            // Retrieve the month from the schedule's date
                            String month = schedule.getDate().split("-")[0]; // Assuming the date is in "yyyy-MM-dd" format

                            // Filter by selected month
                            if (month.equals(getMonthNumber(selectedMonth))) {
                                list.add(schedule);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged(); // Notify the adapter about the data change
                }
                if (list.isEmpty()) {
                    updateFilterTextView("No bookings available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void retrieveBookingsByDate(final String selectedDate) {
        if (selectedDate.isEmpty()) {
            list.clear(); // Clear the list
            adapter.notifyDataSetChanged(); // Notify the adapter about the data change
            updateFilterTextView("No Selected Date"); // Update the filterTextView with no selection
        } else {
            DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
            bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear(); // Clear the list before adding new data
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String userId = userSnapshot.getKey(); // Retrieve the user ID
                            for (DataSnapshot bookingSnapshot : userSnapshot.getChildren()) {
                                String bookingId = bookingSnapshot.getKey(); // Retrieve the booking ID
                                Schedule schedule = bookingSnapshot.getValue(Schedule.class);
                                schedule.setUserId(userId); // Set the user ID for the schedule
                                schedule.setBookingId(bookingId); // Set the booking ID for the schedule

                                // Filter by selected date
                                if (schedule.getDate().equals(selectedDate)) {
                                    list.add(schedule);
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged(); // Notify the adapter about the data change
                    if (list.isEmpty()) {
                        updateFilterTextView("No bookings available"); // Update the filterTextView if no bookings found
                    } else {
                        updateFilterTextView("Date: " + selectedDate); // Update the filterTextView with the selected date
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }



    private String getMonthNumber(String monthName) {
        switch (monthName) {
            case "January":
                return "01";
            case "February":
                return "02";
            case "March":
                return "03";
            case "April":
                return "04";
            case "May":
                return "05";
            case "June":
                return "06";
            case "July":
                return "07";
            case "August":
                return "08";
            case "September":
                return "09";
            case "October":
                return "10";
            case "November":
                return "11";
            case "December":
                return "12";
            default:
                return "";
        }
    }
}
