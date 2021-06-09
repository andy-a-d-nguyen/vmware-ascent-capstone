package com.galvanize.useraccounts;

import javax.validation.constraints.Size;

public class User {
    private Long id;

    @Size(max = 20)
    private String username;
    private String password;
    private String email;
    private String address;
    private String creditCard;
    private boolean verified;

    public User() {
    }

    public User(String username, String password, String email) {
//        if (username.length() <= 20) {
            this.username = username;
            this.password = password;
            this.email = email;
//        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
