package com.example.anthony.softwareeng;

/**
 * Created by Anthony on 12/04/2016.
 */
public class User {
    private String name;
    private String profileImageURL;
    private String admin;
    private String spaceNum;
    private String licencePlateNum;
    private String time;
    public User(){

    }

    public User(String name, String profileImageURL,String admin){
        this.name=name;
        this.profileImageURL=profileImageURL;
        this.admin = admin;
        this.spaceNum = "null";
        this.licencePlateNum = "null";
        this.time = "null";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }


    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }
    public String getSpaceNum() {
        return spaceNum;
    }

    public void setSpaceNum(String spaceNum) {
        this.spaceNum = spaceNum;
    }

    public String getLicencePlateNum() {
        return licencePlateNum;
    }

    public void setLicencePlateNum(String licencePlateNum) {
        this.licencePlateNum = licencePlateNum;
    }
}
