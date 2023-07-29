package com.example.group3projectpetcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {
    private TextView loginTextView;
    private Button confirmButton;
    private EditText usernameEditText, locationEditText, numberEditText, emailEditText, passwordEditText;
    private CheckBox termsCheckBox, showPasswordCheckBox;
    private DatabaseReference databaseUsers;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        loginTextView = findViewById(R.id.loginTextView);
        confirmButton = findViewById(R.id.confirmButton);

        usernameEditText = findViewById(R.id.usernameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        numberEditText = findViewById(R.id.numberEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);
        termsCheckBox = findViewById(R.id.termsCheckBox);

        databaseUsers = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Show password
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // Hide password
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserLogin.class);
                startActivity(intent);
                finish();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profileImagePath = ""; // Initialize the profile image path as an empty string
                InsertData(profileImagePath); // Pass the profile image path to the InsertData() method
            }
        });
    }

    private void InsertData(String profileImagePath) {
        String username = usernameEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String number = numberEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String id = databaseUsers.push().getKey();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(Registration.this, "Enter Username", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(location)) {
            Toast.makeText(Registration.this, "Enter Location", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(number)) {
            Toast.makeText(Registration.this, "Enter Number", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(Registration.this, "Enter Email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(Registration.this, "Enter Password", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isEmailValid(email)) {
            Toast.makeText(Registration.this, "Invalid email address.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isNumberValid(number)) {
            Toast.makeText(Registration.this, "Invalid number format.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isPasswordValid(password)) {
            Toast.makeText(Registration.this, "Invalid password. Password must contain at least 8 characters, one uppercase letter, one lowercase letter, and one digit.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!termsCheckBox.isChecked()) {
            Toast.makeText(Registration.this, "Please accept the terms and conditions.", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();
                                User user = new User(username, location, number, email, null, profileImagePath);// Pass the profile image path to the User constructor
                                user.setPassword(null);
                                databaseUsers.child("users").child(userId).setValue(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(Registration.this, "Account created and data saved", Toast.LENGTH_SHORT).show();

                                                    // Reset EditText fields and checkboxes
                                                    usernameEditText.setText("");
                                                    locationEditText.setText("");
                                                    numberEditText.setText("");
                                                    emailEditText.setText("");
                                                    passwordEditText.setText("");
                                                    showPasswordCheckBox.setChecked(false);
                                                    termsCheckBox.setChecked(false);

                                                    Intent intent = new Intent(getApplicationContext(), UserLogin.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(Registration.this, "Failed to save registration data", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(Registration.this, "Email has already used.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isPasswordValid(String password) {
        // Password composition rules
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()\\-_+=;:,.?/]{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private boolean isEmailValid(String email) {
        // Email validation pattern
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isNumberValid(String number) {
        // Number validation pattern
        String regex = "^09\\d{9}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }
}
