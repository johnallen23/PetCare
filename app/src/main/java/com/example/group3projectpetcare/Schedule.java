package com.example.group3projectpetcare;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Schedule {

    private String userId;
    private String bookingId;
    private String ownerName;
    private String ownerEmail;
    private String ownerContact;
    private String ownerAddress;
    private String petName;
    private String petClass;
    private String petAge;
    private String petBirth;
    private String petGender;
    private String petBreed;
    private String services;
    private String date;
    private String time;
    private String status;
    private String imageUrl;
    private boolean adminActionTaken;
    private String reason;
    private String price;

    public Schedule() {
    }



    public Schedule(String userId, String bookingId, String ownerName, String ownerEmail, String ownerContact, String ownerAddress, String petName, String petClass, String petAge, String petBirth, String petGender, String petBreed, String services, String date, String time, String status, String imageUrl, String reason, String price) {
        this.userId = userId;
        this.bookingId = bookingId;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
        this.ownerContact = ownerContact;
        this.ownerAddress = ownerAddress;
        this.petName = petName;
        this.petClass = petClass;
        this.petAge = petAge;
        this.petBirth = petBirth;
        this.petGender = petGender;
        this.petBreed = petBreed;
        this.services = services;
        this.date = date;
        this.time = time;
        this.status = status;
        this.imageUrl = imageUrl;
        this.reason = reason;
        this.price = price;


    }

    public String getUserId() {
        return userId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getOwnerContact() {
        return ownerContact;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public String getPetName() {
        return petName;
    }

    public String getPetClass() {
        return petClass;
    }

    public String getPetAge() {
        return petAge;
    }

    public String getPetBirth() {return petBirth;}

    public String getPetGender() {
        return petGender;
    }

    public String getPetBreed() {
        return petBreed;
    }

    public String getPrice() {return price;}

    public String getServices() {
        return services;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getReason() {
        return reason;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAdminActionTaken() {
        return adminActionTaken;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public void setOwnerContact(String ownerContact) {
        this.ownerContact = ownerContact;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public void setPetClass(String petClass) {
        this.petClass = petClass;
    }

    public void setPetAge(String petAge) {
        this.petAge = petAge;
    }

    public void setPetGender(String petGender) {
        this.petGender = petGender;
    }

    public void setPetBreed(String petBreed) {
        this.petBreed = petBreed;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAdminActionTaken(boolean adminActionTaken) {
        this.adminActionTaken = adminActionTaken;
    }
}
