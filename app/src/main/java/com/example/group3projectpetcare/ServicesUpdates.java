package com.example.group3projectpetcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ServicesUpdates extends AppCompatActivity {
    private Button updatesbackButton, changeButton, saveButton;
    private ImageView updatesImageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference updatesReference;
    private FirebaseAuth mAuth;
    private TextView DateAndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_updates);
        updatesbackButton = findViewById(R.id.updatesbackButton);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        updatesReference = FirebaseDatabase.getInstance().getReference().child("updates");
        mAuth = FirebaseAuth.getInstance();

        changeButton = findViewById(R.id.changeButton);
        saveButton = findViewById(R.id.saveButton);

        updatesImageView = findViewById(R.id.updatesImageView);
        DateAndTime = findViewById(R.id.DateAndTime);

        updatesbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdminMainInterface.class);
                startActivity(intent);
                finish();
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageToFirebase();
            }
        });

        updatesReference = FirebaseDatabase.getInstance().getReference().child("updates").child("update");
        updatesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("update")) {
                    String imageUrl = dataSnapshot.child("update").getValue(String.class);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Picasso.get().load(imageUrl).into(updatesImageView);
                    }
                }

                if (dataSnapshot.hasChild("timestamp")) {
                    long timestamp = dataSnapshot.child("timestamp").getValue(Long.class);
                    if (timestamp != 0) {
                        Date uploadDate = new Date(timestamp);
                        String formattedDate = DateFormat.getDateInstance().format(uploadDate);
                        String formattedTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(uploadDate);
                        String displayDateTime = "Date: " + formattedDate + "   -   " +"Time: " + formattedTime;
                        DateAndTime.setText(displayDateTime);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Picasso.get().load(selectedImageUri).into(updatesImageView);
        }
    }

    private void saveImageToFirebase() {
        if (selectedImageUri != null) {
            StorageReference imageRef = storageReference.child("updates");
            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL of the uploaded image
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    // Save the image URI and timestamp to the Realtime Database under "updates"
                                    updatesReference.child("update").setValue(downloadUri.toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Set the timestamp
                                                    long timestamp = System.currentTimeMillis();
                                                    updatesReference.child("timestamp").setValue(timestamp)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(ServicesUpdates.this, "Image saved successfully", Toast.LENGTH_SHORT).show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(ServicesUpdates.this, "Failed to save image", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ServicesUpdates.this, "Failed to save image", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ServicesUpdates.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}
