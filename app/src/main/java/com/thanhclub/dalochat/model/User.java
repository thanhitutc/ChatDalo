package com.thanhclub.dalochat.model;


public class User {
    private int idUser;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String logo;
    private String dateBirth;
    private int sex;
    private String phoneNumber;
    private String description;
    private String status;
    private String timeOff;

    public User(int idUser, String userName, String password, String firstName, String lastName,
                String logo, String dateBirth, int sex, String phoneNumber, String description, String status, String timeOff) {
        this.idUser = idUser;
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.logo = logo;
        this.dateBirth = dateBirth;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.status = status;
        this.timeOff = timeOff;
    }


    public int getIdUser() {
        return idUser;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLogo() {
        return logo;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public int getSex() {
        return sex;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getTimeOff() {
        return timeOff;
    }
}
