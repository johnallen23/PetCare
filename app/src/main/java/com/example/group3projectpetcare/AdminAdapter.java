package com.example.group3projectpetcare;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Schedule> list;

    public AdminAdapter(Context context, ArrayList<Schedule> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AdminAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.adminentry, parent, false);
        return new AdminAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminAdapter.MyViewHolder holder, int position) {
        Schedule schedule = list.get(position);
        holder.serviceTextView.setText(schedule.getServices());
        holder.priceTextView.setText(schedule.getPrice());
        holder.dateTextView.setText(schedule.getDate());
        holder.timeTextView.setText(schedule.getTime());

        holder.ownerNameTextView.setText(schedule.getOwnerName());
        holder.ownerEmailTextView.setText(schedule.getOwnerEmail());
        holder.ownerNumberTextView.setText(schedule.getOwnerContact());
        holder.ownerAddressTextView.setText(schedule.getOwnerAddress());

        holder.petNameTextView.setText(schedule.getPetName());
        holder.petClassTextView.setText(schedule.getPetClass());
        holder.petAgeTextView.setText(schedule.getPetAge());
        holder.petGenderTextView.setText(schedule.getPetGender());
        holder.petBreedTextView.setText(schedule.getPetBreed());

        holder.statusTextView.setText(schedule.getStatus());
        holder.reasonTextView.setText(schedule.getReason());

        Button approveButton = holder.itemView.findViewById(R.id.approveButton);
        Button disapproveButton = holder.itemView.findViewById(R.id.disapproveButton);
        Button acceptButton = holder.itemView.findViewById(R.id.acceptButton);
        Button declineButton = holder.itemView.findViewById(R.id.declineButton);



        TextView reason1TextView = holder.itemView.findViewById(R.id.reason1TextView);

        String status = schedule.getStatus();

        if (status.equals("Pending")) {
            acceptButton.setVisibility(View.VISIBLE);
            declineButton.setVisibility(View.VISIBLE);
            approveButton.setVisibility(View.GONE);
            disapproveButton.setVisibility(View.GONE);
            reason1TextView.setVisibility(View.GONE);
            holder.reasonTextView.setVisibility(View.GONE);
        } else if (status.equals("Waiting for Approval")) {
            holder.reasonTextView.setVisibility(View.VISIBLE);
            reason1TextView.setVisibility(View.VISIBLE);
            approveButton.setVisibility(View.VISIBLE);
            disapproveButton.setVisibility(View.VISIBLE);
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
        } else if (status.equals("Cancellation Approved") || status.equals("Cancellation disapproved")) {
            holder.reasonTextView.setVisibility(View.VISIBLE);
            reason1TextView.setVisibility(View.VISIBLE);
            approveButton.setVisibility(View.GONE);
            disapproveButton.setVisibility(View.GONE);
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
        } else {
            // Handle other status conditions if needed
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
            approveButton.setVisibility(View.GONE);
            disapproveButton.setVisibility(View.GONE);
            reason1TextView.setVisibility(View.GONE);
            holder.reasonTextView.setVisibility(View.GONE);
        }






        // Load image using Picasso
        String imageUrl = schedule.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(holder.medicalRecordImageView);
            holder.medicalRecordImageView.setVisibility(View.VISIBLE);
        } else {
            holder.medicalRecordImageView.setVisibility(View.GONE);
        }

// Set click listener for the medicalRecordImageView
        holder.medicalRecordImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to open the full-screen image activity
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("imageUrl", imageUrl); // Pass the image URL to the intent
                context.startActivity(intent);
            }
        });

        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!schedule.isAdminActionTaken()) {

                    String userId = schedule.getUserId();
                    String bookingId = schedule.getBookingId();
                    if (userId != null && bookingId != null) {
                        updateBookingStatus(userId, bookingId, "Cancellation Approved", schedule);
                    } else {
                        // Handle null user ID or booking ID
                        Toast.makeText(context, "Failed to retrieve user ID or booking ID.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        disapproveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!schedule.isAdminActionTaken()) {

                    String userId = schedule.getUserId();
                    String bookingId = schedule.getBookingId();
                    if (userId != null && bookingId != null) {
                        updateBookingStatus(userId, bookingId, "Cancellation disapproved", schedule);
                    } else {
                        // Handle null user ID or booking ID
                        Toast.makeText(context, "Failed to retrieve user ID or booking ID.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



        // Set click listeners for buttons
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!schedule.isAdminActionTaken()) {
                    // Handle accept button click
                    // Update status to "Accepted"
                    String userId = schedule.getUserId();
                    String bookingId = schedule.getBookingId();
                    if (userId != null && bookingId != null) {
                        updateBookingStatus(userId, bookingId, "Accepted", schedule);
                    } else {
                        // Handle null user ID or booking ID
                        Toast.makeText(context, "Failed to retrieve user ID or booking ID.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Notify the admin that they have already taken action on this request
                    Toast.makeText(context, "You have already accepted this request.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!schedule.isAdminActionTaken()) {
                    // Handle decline button click
                    // Update status to "Declined"
                    String userId = schedule.getUserId();
                    String bookingId = schedule.getBookingId();
                    if (userId != null && bookingId != null) {
                        updateBookingStatus(userId, bookingId, "Declined", schedule);
                    } else {
                        // Handle null user ID or booking ID
                        Toast.makeText(context, "Failed to retrieve user ID or booking ID.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Notify the admin that they have already taken action on this request
                    Toast.makeText(context, "You have already declined this request.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }





    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView serviceTextView, priceTextView, dateTextView, timeTextView;
        TextView ownerNameTextView, ownerEmailTextView, ownerNumberTextView, ownerAddressTextView;
        TextView petNameTextView, petClassTextView, petAgeTextView, petGenderTextView, petBreedTextView;
        TextView statusTextView;
        TextView reasonTextView;
        ImageView medicalRecordImageView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceTextView = itemView.findViewById(R.id.serviceTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);

            ownerNameTextView = itemView.findViewById(R.id.ownerNameTextView);
            ownerEmailTextView = itemView.findViewById(R.id.ownerEmailTextView);
            ownerNumberTextView = itemView.findViewById(R.id.ownerNumberTextView);
            ownerAddressTextView = itemView.findViewById(R.id.ownerAddressTextView);

            petNameTextView = itemView.findViewById(R.id.petNameTextView);
            petClassTextView = itemView.findViewById(R.id.petClassTextView);
            petAgeTextView = itemView.findViewById(R.id.petAgeTextView);
            petGenderTextView = itemView.findViewById(R.id.petGenderTextView);
            petBreedTextView = itemView.findViewById(R.id.petBreedTextView);

            statusTextView = itemView.findViewById(R.id.statusTextView);
            medicalRecordImageView = itemView.findViewById(R.id.medicalRecordImageView);
            reasonTextView = itemView.findViewById(R.id.reasonTextView);
        }
    }

    public void updateBookingStatus(String userId, String bookingId, String status, Schedule schedule) {
        if (userId != null && bookingId != null) {
            DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("bookings")
                    .child(userId)
                    .child(bookingId)
                    .child("status");

            bookingRef.setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Status update successful
                        schedule.setStatus(status); // Update the status locally in the Schedule object
                        notifyDataSetChanged();
                        Toast.makeText(context, "Status updated successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Status update failed
                        Toast.makeText(context, "Failed to update status.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }



}