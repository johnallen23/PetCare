package com.example.group3projectpetcare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<NewPet> list;
    private DatabaseReference databaseReference;
    private String userId;

    public PetAdapter(Context context, ArrayList<NewPet> list) {
        this.context = context;
        this.list = list;
        this.databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            this.userId = currentUser.getUid();
        } else {
            // Handle the situation when the user is not authenticated or signed in
            // For example, you can show a login screen or display an error message.
            // In this case, you might set userId to a default value or throw an exception.
            // For demonstration purposes, I'll just show a Toast message.
            Toast.makeText(context, "Error: User is not signed in.", Toast.LENGTH_SHORT).show();
            // You can set a default value for userId if needed:
            // this.userId = "default_user_id";
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.petentry, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NewPet newpet = list.get(position);
        holder.petNameTextView.setText(newpet.getPetname());
        holder.petClassTextView.setText(newpet.getPetclass());
        holder.petAgeTextView.setText(newpet.getPetage());
        holder.petGenderTextView.setText(newpet.getPetgender());
        holder.petBreedTextView.setText(newpet.getPetbreed());
        holder.petBirthdayTextView.setText(newpet.getPetbirthday());

        ImageView delete = holder.itemView.findViewById(R.id.delete);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Get the pet ID based on the position
                    String petId = list.get(position).getPetId();

                    if (petId != null) {
                        deletePetFromDatabase(petId, position); // Pass the position to the deletePetFromDatabase method
                    } else {
                        Toast.makeText(context, "Error: Pet ID is null.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void deletePetFromDatabase(String petId, int position) {
        if (userId == null) {
            Toast.makeText(context, "Error: User ID is null.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userPetsRef = databaseReference.child("MyPets").child(userId).child(petId);
        userPetsRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Pet deleted successfully!", Toast.LENGTH_SHORT).show();
                        // Remove the pet from the list and notify the adapter of the removal
                        list.remove(position);
                        notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to delete pet.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView petNameTextView;
        TextView petClassTextView;
        TextView petAgeTextView;
        TextView petGenderTextView;
        TextView petBreedTextView;
        TextView petBirthdayTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            petNameTextView = itemView.findViewById(R.id.petNameTextView);
            petClassTextView = itemView.findViewById(R.id.petClassTextView);
            petBirthdayTextView = itemView.findViewById(R.id.petBirthdayTextView);
            petAgeTextView = itemView.findViewById(R.id.petAgeTextView);
            petGenderTextView = itemView.findViewById(R.id.petGenderTextView);
            petBreedTextView = itemView.findViewById(R.id.petBreedTextView);
        }
    }
}
