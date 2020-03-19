package com.example.friendspace.Model;

public class User {

    // member variables
    private String id;
    private String firstName;
    private String lastName;
    private String imageURL;

    // constructors
    public  User() {}

    public User(String id, String firstName, String lastName, String imageURL) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageURL = imageURL;
    }

    // getter and setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}
