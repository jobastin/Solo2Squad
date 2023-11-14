package com.example.solo2squad;

public class event {
    private String gameType;
    private String location;
    private String dateAndTime;
    private int slots;
    private double pricePerPerson;
    private String description;

    // Add constructors and getters/setters as needed

    public event() {
        // Default constructor required for calls to DataSnapshot.getValue(event.class)
    }

    public event(String gameType, String location, String dateAndTime, int slots, double pricePerPerson, String description) {
        this.gameType = gameType;
        this.location = location;
        this.dateAndTime = dateAndTime;
        this.slots = slots;
        this.pricePerPerson = pricePerPerson;
        this.description = description;
    }

    public String getGameType() {
        return gameType;
    }

    public String getLocation() {
        return location;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public int getSlots() {
        return slots;
    }

    public double getPricePerPerson() {
        return pricePerPerson;
    }

    public String getDescription() {
        return description;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public void setPricePerPerson(double pricePerPerson) {
        this.pricePerPerson = pricePerPerson;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
