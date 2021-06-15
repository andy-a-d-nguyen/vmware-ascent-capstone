package com.galvanize.useraccounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.galvanize.useraccounts.model.User;

import java.util.ArrayList;
import java.util.List;

public class UsersList {
    private List<User> users;

    public UsersList() {}

    public UsersList(List<User> users) {
        this.users = users;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return users.isEmpty();
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public int size() {
        return this.users.size();
    }
}
