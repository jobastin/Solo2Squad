package com.example.solo2squad;

public class event {
    private String userID;
    private String sportsCategory;
    private String sportsType;
    private String location;
    private String description;
    private String time;
    private int totalSlots;
    private int slotsAvailable;
    private boolean freeBooking;
    private double pricePerSlot;
    private long timestamp;
    private int activeStatus;
    private EventPayment eventPayment;
    private String payment_status;
    private String key;


    public event(String userID, String sportsCategory, String sportsType, String location, String description, String time, int totalSlots, int slotsAvailable, boolean freeBooking, double pricePerSlot, long timestamp, int activeStatus,String payment_status,EventPayment eventPayment) {
        this.userID = userID;
        this.sportsCategory = sportsCategory;
        this.sportsType = sportsType;
        this.location = location;
        this.description = description;
        this.time = time;
        this.totalSlots=totalSlots;
        this.slotsAvailable = slotsAvailable;
        this.freeBooking = freeBooking;
        this.pricePerSlot = pricePerSlot;
        this.timestamp = timestamp;
        this.activeStatus = activeStatus;
        this.eventPayment = eventPayment;
        this.payment_status = payment_status;
    }

    public event() {

    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }


    public String getSportsCategory() {
        return sportsCategory;
    }

    public void setSportsCategory(String sportsCategory) {
        this.sportsCategory = sportsCategory;
    }

    public String getSportsType() {
        return sportsType;
    }

    public void setSportsType(String sportsType) {
        this.sportsType = sportsType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSlotsAvailable() {
        return slotsAvailable;
    }

    public void setSlotsAvailable(int slotsAvailable) {
        this.slotsAvailable = slotsAvailable;
    }

    public boolean isFreeBooking() {
        return freeBooking;
    }

    public void setFreeBooking(boolean freeBooking) {
        this.freeBooking = freeBooking;
    }

    public double getPricePerSlot() {
        return pricePerSlot;
    }

    public void setPricePerSlot(double pricePerSlot) {
        this.pricePerSlot = pricePerSlot;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(int activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public EventPayment getEventPayment() {
        return eventPayment;
    }

    public void setEventPayment(EventPayment eventPayment) {
        this.eventPayment = eventPayment;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }
}
