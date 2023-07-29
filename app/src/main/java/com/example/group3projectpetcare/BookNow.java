package com.example.group3projectpetcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;

import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BookNow extends AppCompatActivity {
    private Spinner petSpinner;

    private TextView textDate, textTime;
    private Button datePickerButton, timePickerButton, bookbackButton, confirmButton, addImageButton, addpetButton;

    private ImageView medicalRecordImageView;

    private TextView ownerNameTextView;
    private TextView ownerEmailTextView;
    private TextView ownerNumberTextView;
    private TextView ownerAddressTextView;

    private TextView petNameTextView;
    private TextView petClassTextView;
    private TextView petSexTextView;
    private TextView petBreedTextView;
    private TextView petBirthTextView;
    private TextView petAgeTextView;

    private TextView selectTextView;
    private TextView nopetTextView;

    private RadioButton haircutRb;
    private RadioButton bathAndBrushRb;
    private RadioButton nailTrimmingRb;
    private RadioButton package1Rb;
    private RadioButton package2Rb;
    private RadioButton package3Rb;

    private TextView haircutLarge, haircutSmall, haircutStray;
    private TextView BnbLarge, BnbSmall, BnbStray;
    private TextView nailLarge, nailSmall, nailStray;
    private TextView package1Large, package1Small, package1Stray;
    private TextView package2Large, package2Small, package2Stray;
    private TextView package3Large, package3Small, package3Stray;

    private FirebaseUser currentUser;
    private DatabaseReference bookingsRef;
    private StorageReference imageStorageRef;
    private Uri selectedImageUri;

    private static final int PICK_IMAGE_REQUEST = 1;
    private String service = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_now);

        petSpinner = findViewById(R.id.petSpinner);
        selectTextView = findViewById(R.id.selectTextView);
        nopetTextView = findViewById(R.id.nopetTextView);

        medicalRecordImageView = findViewById(R.id.medicalRecordImageView);

        ownerNameTextView = findViewById(R.id.ownerNameTextView);
        ownerEmailTextView = findViewById(R.id.ownerEmailTextView);
        ownerNumberTextView = findViewById(R.id.ownerNumberTextView);
        ownerAddressTextView = findViewById(R.id.ownerAddressTextView);

        petNameTextView = findViewById(R.id.petNameTextView);
        petClassTextView = findViewById(R.id.petClassTextView);
        petSexTextView = findViewById(R.id.petSexTextView);
        petBreedTextView = findViewById(R.id.petBreedTextView);
        petBirthTextView = findViewById(R.id.petBirthTextView);
        petAgeTextView = findViewById(R.id.petAgeTextView);

        haircutRb = findViewById(R.id.haircutRb);
        bathAndBrushRb = findViewById(R.id.bathAndBrushRb);
        nailTrimmingRb = findViewById(R.id.nailTrimmingRb);
        package1Rb = findViewById(R.id.package1Rb);
        package2Rb = findViewById(R.id.package2Rb);
        package3Rb = findViewById(R.id.package3Rb);

        haircutLarge = findViewById(R.id.haircutLarge);
        haircutSmall = findViewById(R.id.haircutSmall);
        haircutStray = findViewById(R.id.haircutStray);
        BnbLarge = findViewById(R.id.BnbLarge);
        BnbSmall = findViewById(R.id.BnbSmall);
        BnbStray = findViewById(R.id.BnbStray);
        nailLarge = findViewById(R.id.nailLarge);
        nailSmall = findViewById(R.id.nailSmall);
        nailStray = findViewById(R.id.nailStray);
        package1Large = findViewById(R.id.package1Large);
        package1Small = findViewById(R.id.package1Small);
        package1Stray = findViewById(R.id.package1Stray);
        package2Large = findViewById(R.id.package2Large);
        package2Small = findViewById(R.id.package2Small);
        package2Stray = findViewById(R.id.package2Stray);
        package3Large = findViewById(R.id.package3Large);
        package3Small = findViewById(R.id.package3Small);
        package3Stray = findViewById(R.id.package3Stray);


        textDate = findViewById(R.id.textDate);
        textTime = findViewById(R.id.textTime);
        timePickerButton = findViewById(R.id.timePickerButton);
        datePickerButton = findViewById(R.id.datePickerButton);
        bookbackButton = findViewById(R.id.bookbackButton);
        confirmButton = findViewById(R.id.confirmButton);
        addImageButton = findViewById(R.id.addImageButton);
        addpetButton = findViewById(R.id.addpetButton);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
        imageStorageRef = FirebaseStorage.getInstance().getReference("images");


        // Hide all TextViews initially
        haircutLarge.setVisibility(View.GONE);
        haircutSmall.setVisibility(View.GONE);
        haircutStray.setVisibility(View.GONE);
        BnbLarge.setVisibility(View.GONE);
        BnbSmall.setVisibility(View.GONE);
        BnbStray.setVisibility(View.GONE);
        nailLarge.setVisibility(View.GONE);
        nailSmall.setVisibility(View.GONE);
        nailStray.setVisibility(View.GONE);
        package1Large.setVisibility(View.GONE);
        package1Small.setVisibility(View.GONE);
        package1Stray.setVisibility(View.GONE);
        package2Large.setVisibility(View.GONE);
        package2Small.setVisibility(View.GONE);
        package2Stray.setVisibility(View.GONE);
        package3Large.setVisibility(View.GONE);
        package3Small.setVisibility(View.GONE);
        package3Stray.setVisibility(View.GONE);

        petBreedTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setTextViewsVisibility(View.GONE);
                uncheckRadioButtons();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        haircutRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    service = "Haircut";
                    updateTextViewVisibility();
                }
            }
        });

        bathAndBrushRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    service = "Bath and Brush";
                    updateTextViewVisibility();
                }
            }
        });

        nailTrimmingRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    service = "Nail Trimming";
                    updateTextViewVisibility();
                }
            }
        });

        package1Rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    service = "Package 1";
                    updateTextViewVisibility();
                }
            }
        });

        package2Rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    service = "Package 2";
                    updateTextViewVisibility();
                }
            }
        });

        package3Rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    service = "Package 3";
                    updateTextViewVisibility();
                }
            }
        });

        bookbackButton.setOnClickListener(new View.OnClickListener() {
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

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertData();
            }
        });

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDate();
            }
        });

        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTime();
            }
        });

        // Retrieve user information from Firebase
        retrieveUserInformation();

        // Populate the pet spinner
        populatePetSpinner();

        // Set the initial pet information based on the first pet in the spinner
        if (petSpinner.getSelectedItem() != null) {
            String selectedPetName = petSpinner.getSelectedItem().toString();
            displayPetInformation(selectedPetName);
        }

        // Listen for spinner item selection and update the pet information accordingly
        petSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPetName = parent.getItemAtPosition(position).toString();
                displayPetInformation(selectedPetName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Check if there are pets to retrieve
        checkPetsExistence();
    }

    private void updateTextViewVisibility() {
        String petBreed = petBreedTextView.getText().toString().trim();

        // Hide all TextViews
        setTextViewsVisibility(View.GONE);

        switch (service) {
            case "Haircut":
                if (petBreed.equals("Large Breed")) {
                    haircutLarge.setVisibility(View.VISIBLE);
                } else if (petBreed.equals("Small Breed")) {
                    haircutSmall.setVisibility(View.VISIBLE);
                } else {
                    haircutStray.setVisibility(View.VISIBLE);
                }
                break;
            case "Bath and Brush":
                if (petBreed.equals("Large Breed")) {
                    BnbLarge.setVisibility(View.VISIBLE);
                } else if (petBreed.equals("Small Breed")) {
                    BnbSmall.setVisibility(View.VISIBLE);
                } else {
                    BnbStray.setVisibility(View.VISIBLE);
                }
                break;
            case "Nail Trimming":
                if (petBreed.equals("Large Breed")) {
                    nailLarge.setVisibility(View.VISIBLE);
                } else if (petBreed.equals("Small Breed")) {
                    nailSmall.setVisibility(View.VISIBLE);
                } else {
                    nailStray.setVisibility(View.VISIBLE);
                }
                break;
            case "Package 1":
                if (petBreed.equals("Large Breed")) {
                    package1Large.setVisibility(View.VISIBLE);
                } else if (petBreed.equals("Small Breed")) {
                    package1Small.setVisibility(View.VISIBLE);
                } else {
                    package1Stray.setVisibility(View.VISIBLE);
                }
                break;
            case "Package 2":
                if (petBreed.equals("Large Breed")) {
                    package2Large.setVisibility(View.VISIBLE);
                } else if (petBreed.equals("Small Breed")) {
                    package2Small.setVisibility(View.VISIBLE);
                } else {
                    package2Stray.setVisibility(View.VISIBLE);
                }
                break;
            case "Package 3":
                if (petBreed.equals("Large Breed")) {
                    package3Large.setVisibility(View.VISIBLE);
                } else if (petBreed.equals("Small Breed")) {
                    package3Small.setVisibility(View.VISIBLE);
                } else {
                    package3Stray.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void uncheckRadioButtons() {
        haircutRb.setChecked(false);
        bathAndBrushRb.setChecked(false);
        nailTrimmingRb.setChecked(false);
        package1Rb.setChecked(false);
        package2Rb.setChecked(false);
        package3Rb.setChecked(false);
    }


    private void setTextViewsVisibility(int visibility) {
        haircutLarge.setVisibility(visibility);
        haircutSmall.setVisibility(visibility);
        haircutStray.setVisibility(visibility);
        BnbLarge.setVisibility(visibility);
        BnbSmall.setVisibility(visibility);
        BnbStray.setVisibility(visibility);
        nailLarge.setVisibility(visibility);
        nailSmall.setVisibility(visibility);
        nailStray.setVisibility(visibility);
        package1Large.setVisibility(visibility);
        package1Small.setVisibility(visibility);
        package1Stray.setVisibility(visibility);
        package2Large.setVisibility(visibility);
        package2Small.setVisibility(visibility);
        package2Stray.setVisibility(visibility);
        package3Large.setVisibility(visibility);
        package3Small.setVisibility(visibility);
        package3Stray.setVisibility(visibility);
    }



    private void retrieveUserInformation() {
        String userId = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        ownerNameTextView.setText(user.getUsername());
                        ownerEmailTextView.setText(user.getEmail());
                        ownerNumberTextView.setText(user.getNumber());
                        ownerAddressTextView.setText(user.getLocation());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BookNow.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populatePetSpinner() {
        String userId = currentUser.getUid();
        DatabaseReference petsRef = FirebaseDatabase.getInstance().getReference("MyPets").child(userId);
        petsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> petNames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NewPet pet = snapshot.getValue(NewPet.class);
                    if (pet != null) {
                        String petName = pet.getPetname();
                        petNames.add(petName);
                    }
                }

                if (!petNames.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(BookNow.this, android.R.layout.simple_spinner_item, petNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    petSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BookNow.this, "Failed to retrieve pet data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPetInformation(String petName) {
        String userId = currentUser.getUid();
        DatabaseReference petsRef = FirebaseDatabase.getInstance().getReference("MyPets").child(userId);
        Query query = petsRef.orderByChild("petname").equalTo(petName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        NewPet pet = snapshot.getValue(NewPet.class);
                        if (pet != null) {
                            petNameTextView.setText(pet.getPetname());
                            petClassTextView.setText(pet.getPetclass());
                            petSexTextView.setText(pet.getPetgender());
                            petBreedTextView.setText(pet.getPetbreed());
                            petBirthTextView.setText(pet.getPetbirthday());
                            petAgeTextView.setText(pet.getPetage());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BookNow.this, "Failed to retrieve pet data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPetsExistence() {
        String userId = currentUser.getUid();
        DatabaseReference petsRef = FirebaseDatabase.getInstance().getReference("MyPets").child(userId);
        petsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Pets exist
                    petSpinner.setVisibility(View.VISIBLE);
                    selectTextView.setVisibility(View.VISIBLE);
                    addpetButton.setVisibility(View.GONE);
                    nopetTextView.setVisibility(View.GONE);
                } else {
                    // No pets
                    petSpinner.setVisibility(View.GONE);
                    selectTextView.setVisibility(View.GONE);
                    addpetButton.setVisibility(View.VISIBLE);
                    nopetTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BookNow.this, "Failed to retrieve pet data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openDate() {
        // Get the current date
        Calendar currentDate = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH);
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // Adjust month and day values to display as two digits if necessary
                String formattedMonth = String.format("%02d", month + 1);
                String formattedDay = String.format("%02d", day);

                // Format the date as "month-day-year"
                String selectedDate = formattedMonth + "-" + formattedDay + "-" + year;

                // Check if selected date is in the past
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, day);

                if (selectedCalendar.before(currentDate)) {
                    Toast.makeText(BookNow.this, "That date is already past.", Toast.LENGTH_SHORT).show();
                } else if (selectedCalendar.get(Calendar.YEAR) == currentYear && selectedCalendar.get(Calendar.MONTH) == currentMonth && selectedCalendar.get(Calendar.DAY_OF_MONTH) == currentDay) {
                    Toast.makeText(BookNow.this, "You cannot select today.", Toast.LENGTH_SHORT).show();
                } else if (selectedCalendar.get(Calendar.YEAR) == currentYear && selectedCalendar.get(Calendar.MONTH) == currentMonth && selectedCalendar.get(Calendar.DAY_OF_MONTH) == currentDay + 1) {
                    Toast.makeText(BookNow.this, "You cannot select tomorrow.", Toast.LENGTH_SHORT).show();
                } else {
                    textDate.setText(selectedDate);
                }

            }
        }, currentYear, currentMonth, currentDay);

        // Set minimum date as the current date
        dialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());

        dialog.show();
    }


    private void openTime() {
        TimePickerDialog time = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                // Check if it is AM or PM
                String amPm = (hours < 12) ? "AM" : "PM";

                // Convert 24-hour format to 12-hour format
                int displayHours = (hours > 12) ? hours - 12 : hours;
                if (displayHours == 0) {
                    displayHours = 12;
                }

                // Adjust minutes to display as two digits if necessary
                String formattedMinutes = String.format("%02d", minutes);

                // Format the time as "hh:mm AM/PM"
                String selectedTime = String.valueOf(displayHours) + ":" + formattedMinutes + " " + amPm;

                // Check if selected time is within operating hours
                if (hours < 8 || (hours == 8 && minutes < 0) || hours >= 17 || (hours == 17 && minutes > 0)) {
                    Toast.makeText(BookNow.this, "Operating Hours is from 8 AM to 5 PM only.", Toast.LENGTH_SHORT).show();
                } else {
                    textTime.setText(selectedTime);
                }
            }
        }, 8, 0, false);

        time.show();
    }


    private void InsertData() {
        // Get the values from TextView fields
        String ownerName = ownerNameTextView.getText().toString();
        String ownerEmail = ownerEmailTextView.getText().toString();
        String ownerContact = ownerNumberTextView.getText().toString();
        String ownerAddress = ownerAddressTextView.getText().toString();
        String petName = petNameTextView.getText().toString();
        String petClass = petClassTextView.getText().toString();
        String petGender = petSexTextView.getText().toString();
        String petBreed = petBreedTextView.getText().toString().trim();
        String petBirth = petBirthTextView.getText().toString();
        String petAge = petAgeTextView.getText().toString();



        final String services;
        final String price;

        if (haircutRb.isChecked()) {
            services = "Haircut";
            if (petBreed.equals("Large Breed")) {
                price = "₱500.00";
            } else if (petBreed.equals("Small Breed")) {
                price = "₱350.00";
            } else {
                price = "₱200.00";
            }
        } else if (bathAndBrushRb.isChecked()) {
            services = "Bath and Brush";
            if (petBreed.equals("Large Breed")) {
                price = "₱600.00";
            } else if (petBreed.equals("Small Breed")) {
                price = "₱400.00";
            } else {
                price = "₱300.00";
            }
        } else if (nailTrimmingRb.isChecked()) {
            services = "Nail Trimming";
            if (petBreed.equals("Large Breed")) {
                price = "₱200.00";
            } else if (petBreed.equals("Small Breed")) {
                price = "₱150.00";
            } else {
                price = "₱100.00";
            }
        } else if (package1Rb.isChecked()) {
            services = "Package 1";
            if (petBreed.equals("Large Breed")) {
                price = "₱800.00";
            } else if (petBreed.equals("Small Breed")) {
                price = "₱650.00";
            } else {
                price = "₱400.00";
            }
        } else if (package2Rb.isChecked()) {
            services = "Package 2";
            if (petBreed.equals("Large Breed")) {
                price = "₱750.00";
            } else if (petBreed.equals("Small Breed")) {
                price = "₱500.00";
            } else {
                price = "₱350.00";
            }
        } else if (package3Rb.isChecked()) {
            services = "Package 3";
            if (petBreed.equals("Large Breed")) {
                price = "₱1200.00";
            } else if (petBreed.equals("Small Breed")) {
                price = "₱800.00";
            } else {
                price = "₱500.00";
            }
        } else {
            Toast.makeText(BookNow.this, "Please select a service.", Toast.LENGTH_SHORT).show();
            return;
        }



        String date = textDate.getText().toString().trim();
        String time = textTime.getText().toString().trim();

        if (selectedImageUri == null) {
            Toast.makeText(BookNow.this, "Please provide a medical record of your pet.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if any required fields are empty
        if (ownerName.isEmpty() || ownerEmail.isEmpty() || ownerContact.isEmpty() ||
                ownerAddress.isEmpty() || petName.isEmpty() || petClass.isEmpty() ||
                petAge.isEmpty() || petGender.isEmpty() || petBreed.isEmpty() || price.isEmpty()||
                date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data to be saved in Firebase
        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("ownerName", ownerName);
        bookingData.put("ownerEmail", ownerEmail);
        bookingData.put("ownerContact", ownerContact);
        bookingData.put("ownerAddress", ownerAddress);
        bookingData.put("petName", petName);
        bookingData.put("petClass", petClass);
        bookingData.put("petAge", petAge);
        bookingData.put("petBirth", petBirth);
        bookingData.put("petGender", petGender);
        bookingData.put("petBreed", petBreed);
        bookingData.put("services", services);
        bookingData.put("price", price);
        bookingData.put("date", date);
        bookingData.put("time", time);
        bookingData.put("status", "Pending"); // Add the status field and set it to "Pending"
        bookingData.put("reason", "For Cancelled bookings ONLY.");

        String bookingId = bookingsRef.child(currentUser.getUid()).push().getKey(); // Generate a unique key for the booking
        String imageId = UUID.randomUUID().toString();
        StorageReference imageRef = imageStorageRef.child(imageId);
        UploadTask uploadTask = imageRef.putFile(selectedImageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        bookingData.put("imageUrl", imageUrl);

                        // Save the booking data to the Realtime Database
                        bookingsRef.child(currentUser.getUid()).child(bookingId).setValue(bookingData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(BookNow.this, "Booking successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Appointments.class);
                                    startActivity(intent);
                                    finish();
                                    clearForm();
                                } else {
                                    Toast.makeText(BookNow.this, "Failed to book", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BookNow.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // Specify the MIME type of the content you want to pick (e.g., images)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"}); // Additional MIME types if needed
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Load the selected image into the ImageView using Picasso
            Picasso.get().load(selectedImageUri).into(medicalRecordImageView);
        }
    }


    private void clearForm() {
        ownerNameTextView.setText("");
        ownerEmailTextView.setText("");
        ownerNumberTextView.setText("");
        ownerAddressTextView.setText("");
        petNameTextView.setText("");
        petClassTextView.setText("");
        petSexTextView.setText("");
        petBreedTextView.setText("");
        petBirthTextView.setText("");
        petAgeTextView.setText("");

        haircutRb.setChecked(false);
        bathAndBrushRb.setChecked(false);
        nailTrimmingRb.setChecked(false);
        package1Rb.setChecked(false);
        package2Rb.setChecked(false);
        package3Rb.setChecked(false);

        textDate.setText("");
        textTime.setText("");

        medicalRecordImageView.setImageDrawable(null); // Remove the image
    }

}
