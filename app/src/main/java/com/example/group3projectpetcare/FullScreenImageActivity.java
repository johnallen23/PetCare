package com.example.group3projectpetcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class FullScreenImageActivity extends AppCompatActivity {
    private Button fullbackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        fullbackButton = findViewById(R.id.fullbackButton);

        // Get the image URL from the intent
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Load the image into the ImageView using Picasso
        ImageView imageView = findViewById(R.id.fullScreenImageView);
        Picasso.get().load(imageUrl).into(imageView);

        fullbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScheduleView.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
