package com.galvanize.useraccounts;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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
}
