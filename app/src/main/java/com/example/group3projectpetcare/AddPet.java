package com.example.group3projectpetcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AddPet extends AppCompatActivity {

    private EditText petNameEditText;
    private RadioButton dogRadioButton, catRadioButton, maleRadioButton, femaleRadioButton, largeRadioButton, smallRadioButton, strayRadioButton;
    private Button addbackButton, confirmButton, datePickerButton;
    private TextView petAgeTextView, textDate;
    private DatabaseReference databasePets;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        addbackButton = findViewById(R.id.addbackButton);
        confirmButton = findViewById(R.id.confirmButton);
        datePickerButton = findViewById(R.id.datePickerButton);

        petNameEditText = findViewById(R.id.petNameEditText);
        dogRadioButton = findViewById(R.id.DogradioButton);
        catRadioButton = findViewById(R.id.CatradioButton);
        maleRadioButton = findViewById(R.id.MaleradioButton);
        femaleRadioButton = findViewById(R.id.FemaleradioButton);
        largeRadioButton = findViewById(R.id.LargeradioButton);
        smallRadioButton = findViewById(R.id.SmallradioButton);
        strayRadioButton = findViewById(R.id.StrayradioButton);

        textDate = findViewById(R.id.textDate);
        petAgeTextView = findViewById(R.id.petAgeTextView);

        databasePets = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        addbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPets.class);
                startActivity(intent);
                finish();
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
    }

    private void openDate() {
        // Get the current date from the Calendar
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddPet.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Update the 'textDate' TextView with the selected date
                String formattedDate = String.format(Locale.US, "%02d-%02d-%04d", monthOfYear + 1, dayOfMonth, year);
                textDate.setText(formattedDate);

                // Calculate the age based on the selected date
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, monthOfYear, dayOfMonth);
                Calendar currentDate = Calendar.getInstance();

                // Check if the selected date is ahead of the current date
                if (selectedDate.after(currentDate)) {
                    // Show toast with "Invalid Birth Date" message
                    Toast.makeText(AddPet.this, "Invalid Birth Date", Toast.LENGTH_SHORT).show();
                    return; // Exit the method if the date is invalid
                }

                int years = currentDate.get(Calendar.YEAR) - selectedDate.get(Calendar.YEAR);
                int months = currentDate.get(Calendar.MONTH) - selectedDate.get(Calendar.MONTH);

                // Adjust the age if necessary
                if (months < 0) {
                    years--;
                    months += 12;
                }

                // Update the 'petAgeTextView' with the calculated age
                String age = "";
                if (years > 0) {
                    age += years + (years > 1 ? " years" : " year");
                }
                if (months > 0) {
                    age += (years > 0 ? ", " : "") + months + (months > 1 ? " months" : " month");
                }
                petAgeTextView.setText(age);
            }
        }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }


    private void InsertData() {
        String petname = petNameEditText.getText().toString().trim();
        String petage = petAgeTextView.getText().toString().trim();
        String petbirthday = textDate.getText().toString().trim();




        if (TextUtils.isEmpty(petname)) {
            Toast.makeText(AddPet.this, "Please enter your pet's name.", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(petage)) {
            Toast.makeText(AddPet.this, "Please select the pet's birthday to determine the age.", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(petbirthday)) {
            Toast.makeText(AddPet.this, "Please select the pet's birthday.", Toast.LENGTH_LONG).show();
            return;
        }


        String petclass;
        if (dogRadioButton.isChecked()) {
            petclass = "Dog";
        } else if (catRadioButton.isChecked()) {
            petclass = "Cat";
        } else {
            Toast.makeText(AddPet.this, "Please select a pet class.", Toast.LENGTH_LONG).show();
            return;
        }


        String petgender;
        if (maleRadioButton.isChecked()) {
            petgender = "Male";
        } else if (femaleRadioButton.isChecked()) {
            petgender = "Female";
        } else {
            Toast.makeText(AddPet.this, "Please select a pet gender.", Toast.LENGTH_LONG).show();
            return;
        }


        String petbreed;
        if (largeRadioButton.isChecked()) {
            petbreed = "Large Breed";
        } else if (smallRadioButton.isChecked()) {
            petbreed = "Small Breed";
        } else if (strayRadioButton.isChecked()) {
            petbreed = "Stray Animal";
        } else {
            Toast.makeText(AddPet.this, "Please select a pet breed.", Toast.LENGTH_LONG).show();
            return;
        }

        if (currentUser != null) {
            String uid = currentUser.getUid();
            String id = databasePets.child("MyPets").child(uid).push().getKey();

            HashMap<String, Object> petData = new HashMap<>();
            petData.put("petId", id); // Add the generated pet ID to the HashMap
            petData.put("petname", petname);
            petData.put("petage", petage);
            petData.put("petbirthday", petbirthday);
            petData.put("petclass", petclass);
            petData.put("petgender", petgender);
            petData.put("petbreed", petbreed);

            databasePets.child("MyPets").child(uid).child(id).setValue(petData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddPet.this, "New Pet has been saved.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MyPets.class);
                                startActivity(intent);
                                finish();
                                // Reset EditText fields and checkboxes
                                petNameEditText.setText("");
                                dogRadioButton.setChecked(false);
                                catRadioButton.setChecked(false);
                                maleRadioButton.setChecked(false);
                                femaleRadioButton.setChecked(false);
                                largeRadioButton.setChecked(false);
                                smallRadioButton.setChecked(false);
                                strayRadioButton.setChecked(false);
                                textDate.setText(""); // Clear the selected date as well
                            } else {
                                Toast.makeText(AddPet.this, "Failed to save new pet.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }




}
