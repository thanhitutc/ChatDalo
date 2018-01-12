package com.thanhclub.dalochat.model;



public class Friend {
    private String idUSer;
    private String userName;
    private String firstname;
    private String lastname;
    private String logo;
    private String birthday;
    private String sex;
    private String description;
    private String status;
    private String timeoff;
    private String roomid;
    private String phoneNumber;

    public Friend(String idUSer, String userName, String firstname, String lastname, String logo, String birthday, String sex, String description, String status, String timeoff, String roomid, String phoneNumber) {
        this.idUSer = idUSer;
        this.userName = userName;
        this.firstname = firstname;
        this.lastname = lastname;
        this.logo = logo;
        this.birthday = birthday;
        this.sex = sex;
        this.description = description;
        this.status = status;
        this.timeoff = timeoff;
        this.roomid = roomid;
        this.phoneNumber = phoneNumber;
    }

    public String getIdUSer() {
        return idUSer;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getLogo() {
        return logo;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getSex() {
        return sex;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getTimeoff() {
        return timeoff;
    }

    public String getRoomid() {
        return roomid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
