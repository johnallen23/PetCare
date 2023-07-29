package com.example.group3projectpetcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserMainInterface extends AppCompatActivity {
    private Button profileButton, servicesButton, petsButton, appointmentButton, aboutButton, fixedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_interface);
        profileButton = findViewById(R.id.profileButton);
        servicesButton = findViewById(R.id.servicesButton);
        appointmentButton = findViewById(R.id.appointmentButton);
        petsButton = findViewById(R.id.petsButton);
        aboutButton = findViewById(R.id.aboutButton);
        fixedButton = findViewById(R.id.fixedButton);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyProfile.class);
                startActivity(intent);
                finish();
            }
        });

        petsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPets.class);
                startActivity(intent);
                finish();
            }
        });

        appointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Appointments.class);
                startActivity(intent);
                finish();
            }
        });

        servicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Services.class);
                startActivity(intent);
                finish();
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AboutUs.class);
                startActivity(intent);
                finish();
            }
        });

        fixedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookNow.class);
                startActivity(intent);
                finish();
            }
        });
    }
}