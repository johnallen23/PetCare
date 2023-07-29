package com.example.group3projectpetcare;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.MyViewHolder> {
    Context context;
    ArrayList<Schedule> list;

    public AppointmentAdapter(Context context, ArrayList<Schedule> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AppointmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.bookentry, parent, false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.MyViewHolder holder, int position) {
        Schedule schedule = list.get(position);
        holder.serviceTextView.setText(schedule.getServices());
        holder.priceTextView.setText(schedule.getPrice());
        holder.ownerNameTextView.setText(schedule.getOwnerName());
        holder.ownerEmailTextView.setText(schedule.getOwnerEmail());
        holder.ownerNumberTextView.setText(schedule.getOwnerContact());
        holder.petTextView.setText(schedule.getPetName());
        holder.classTextView.setText(schedule.getPetClass());
        holder.dateTextView.setText(schedule.getDate());
        holder.timeTextView.setText(schedule.getTime());
        holder.statusTextView.setText(schedule.getStatus());
        holder.reasonTextView.setText(schedule.getReason());

        TextView reason1TextView = holder.itemView.findViewById(R.id.reason1TextView);
        String status = schedule.getStatus();

        if (status.equals("Cancellation Approved") || status.equals("Cancellation disapproved")
                || status.equals("Waiting for Approval")){
            holder.reasonTextView.setVisibility(View.VISIBLE);
            reason1TextView.setVisibility(View.VISIBLE);
        } else {
            reason1TextView.setVisibility(View.GONE);
            holder.reasonTextView.setVisibility(View.GONE);
        }

        Button cancelButton = holder.cancelButton;

        if (schedule.getStatus().equals("Pending") || schedule.getStatus().equals("Accepted")) {
            cancelButton.setVisibility(View.VISIBLE);
        } else {
            cancelButton.setVisibility(View.GONE);
        }

        // Parse the booking date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        try {
            Date bookingDate = dateFormat.parse(schedule.getDate());

            // Get the current date
            Calendar currentDate = Calendar.getInstance();

            // Check if the booking date is in the past
            if (bookingDate != null && bookingDate.before(currentDate.getTime())) {
                cancelButton.setVisibility(View.GONE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), cancelBooking.class);
                v.getContext().startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView serviceTextView;
        TextView priceTextView;
        TextView ownerNameTextView;
        TextView ownerEmailTextView;
        TextView ownerNumberTextView;
        TextView petTextView;
        TextView classTextView;
        TextView dateTextView;
        TextView timeTextView;
        TextView statusTextView;
        Button cancelButton;

        TextView reasonTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceTextView = itemView.findViewById(R.id.serviceTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            ownerNameTextView = itemView.findViewById(R.id.ownerNameTextView);
            ownerEmailTextView = itemView.findViewById(R.id.ownerEmailTextView);
            ownerNumberTextView = itemView.findViewById(R.id.ownerNumberTextView);
            petTextView = itemView.findViewById(R.id.petTextView);
            classTextView = itemView.findViewById(R.id.classTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            cancelButton = itemView.findViewById(R.id.cancelButton);

            reasonTextView = itemView.findViewById(R.id.reasonTextView);
        }
    }
}
