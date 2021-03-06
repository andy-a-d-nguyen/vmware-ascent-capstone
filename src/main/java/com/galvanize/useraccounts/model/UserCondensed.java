package com.galvanize.useraccounts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserCondensed {

    private Long guid;
    private String username;
    private String avatar;
    private String email;

    public UserCondensed() {
    }

    public UserCondensed(Long guid, String username, String avatar, String email) {
        this.guid = guid;
        this.username = username;
        this.avatar = avatar;
        this.email = email;
    }

    public Long getGuid() {
        return guid;
    }

    public void setGuid(Long id) {
        this.guid = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}

