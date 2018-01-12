package com.thanhclub.dalochat.model;



public class UserAddFriend {
    private String avatar;
    private String username;
    private String body;
    private String fisrtname;
    private String lastname;
    private String phone;
    private int sex;

    public UserAddFriend(String avatar, String username, String body, String fisrtname, String lastname, String phone, int sex) {
        this.avatar = avatar;
        this.username = username;
        this.body = body;
        this.fisrtname = fisrtname;
        this.lastname = lastname;
        this.phone = phone;
        this.sex = sex;
    }

    public String getUsername() {
        return username;
    }

    public String getBody() {
        return body;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getFisrtname() {
        return fisrtname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPhone() {
        return phone;
    }

    public int getSex() {
        return sex;
    }
}
