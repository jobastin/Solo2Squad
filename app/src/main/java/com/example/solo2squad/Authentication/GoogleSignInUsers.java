package com.example.solo2squad.Authentication;

public class GoogleSignInUsers {


    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private Address address;
    private String userType;
    private int profileSection;
    private String image;

    private String dob;

    public GoogleSignInUsers() {

    }

    public GoogleSignInUsers(String userId, String name, String email, String phoneNumber, Address address, String userType, int profileSection, String image,String dob) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.userType = userType;
        this.profileSection = profileSection;
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getProfileSection() {
        return profileSection;
    }

    public void setProfileSection(int profileSection) {
        this.profileSection = profileSection;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}


