package com.techart.towme.model;

public class Profile {
    private static Profile instance;
    private String email;
    private String uuid;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String registeredAs;
    private String address;
    private String latitude;
    private String longitude;

    public static synchronized Profile getInstance() {
        if (instance == null) {
            instance = new Profile();
        }
        return instance;
    }

    public static void setInstance(Profile instance) {
        Profile.instance = instance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRegisteredAs() {
        return registeredAs;
    }

    public void setRegisteredAs(String registeredAs) {
        this.registeredAs = registeredAs;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
