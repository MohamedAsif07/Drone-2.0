package com.drone.app1;



public class ContactModel {

    private String name;
    private String phoneNo;

    public ContactModel(String name, String phoneNo) {
        this.name = name;
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }
}
