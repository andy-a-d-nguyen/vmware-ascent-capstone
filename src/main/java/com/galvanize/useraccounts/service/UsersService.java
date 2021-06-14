package com.galvanize.useraccounts.service;

import com.galvanize.useraccounts.UsersList;
import com.galvanize.useraccounts.exception.DuplicateUserException;
import com.galvanize.useraccounts.model.User;

import com.galvanize.useraccounts.repository.UsersRepository;
import com.galvanize.useraccounts.request.UserRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {
    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User createUser(User user) {
        Optional<User> foundUser = usersRepository.findByUsernameExactMath(user.getUsername());
        Optional<User> foundUserEmail = usersRepository.findByEmailExactMath(user.getEmail());

        if (foundUser.isPresent() || foundUserEmail.isPresent()) {
            throw new DuplicateUserException();
        }

        return usersRepository.save(user);

    }

    public void deleteUser(Long id) {
        return;
    }

    public User updateUser(Long id, UserRequest updatedUser) { return null; }

    public User getUser(Long id) {
        return null;
    }

    public Boolean updateUserPassword(Long id, String oldPassword, String newPassword) {
        return null;
    }

    public User setAvatar(Long id, String url) {
        return null;
    }

    public UsersList searchUsers(String username) {
        if (username == null) username = "";

        UsersList users = new UsersList(usersRepository.findByUsername("%" + username + "%"));

        return users.isEmpty() ? null : users;
    }

}
