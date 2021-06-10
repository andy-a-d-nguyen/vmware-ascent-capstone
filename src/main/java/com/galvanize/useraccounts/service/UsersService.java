package com.galvanize.useraccounts.service;

import com.galvanize.useraccounts.model.User;
import com.galvanize.useraccounts.repository.UsersRepository;
import com.galvanize.useraccounts.request.UpdateUserRequest;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User createUser(User user) {
       return null;
    }

    public void deleteUser(Long id) {
        return;
    }

    public User updateUser(Long id, UpdateUserRequest updatedUser) { return null; }

    public User getUser(Long id) {
        return null;
    }

    public Boolean updateUserPassword(Long id, String oldPassword, String newPassword) {
        return null;
    }
}
