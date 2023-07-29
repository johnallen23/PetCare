package com.example.group3projectpetcare;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class NewPet {
    private String petId;
    private String petname;
    private String petclass;
    private String petgender;
    private String petbreed;
    private String petbirthday;
    private String petage;

    public NewPet() {
        // Default constructor required for Firebase
    }

    public NewPet(String petId, String petname, String petclass, String petgender, String petbreed, String petbirthday, String petage) {
        this.petId = petId;
        this.petname = petname;
        this.petclass = petclass;
        this.petgender = petgender;
        this.petbreed = petbreed;
        this.petbirthday = petbirthday;
        this.petage = petage;
    }

    public String getPetId() {
        return petId;
    }

    public String getPetname() {
        return petname;
    }

    public String getPetclass() {
        return petclass;
    }

    public String getPetgender() {
        return petgender;
    }

    public String getPetbreed() {
        return petbreed;
    }

    public String getPetbirthday() {
        return petbirthday;
    }

    public String getPetage() {
        return petage;
    }
}
