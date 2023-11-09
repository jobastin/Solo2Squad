package com.example.solo2squad.Authentication;

public class Address {
    private String street;
    private String province;
    private String pin;
    private String city;

    public Address() {
        // Default constructor required for Firebase
    }

    public Address(String street, String province, String pin, String city) {
        this.street = street;
        this.province = province;
        this.pin = pin;
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
