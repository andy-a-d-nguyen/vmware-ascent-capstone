package com.galvanize.useraccounts.service;

import com.galvanize.useraccounts.UsersList;
import com.galvanize.useraccounts.exception.DuplicateUserException;
import com.galvanize.useraccounts.exception.UserNotFoundException;
import com.galvanize.useraccounts.model.User;

import com.galvanize.useraccounts.repository.UsersRepository;
import com.galvanize.useraccounts.request.UserRequest;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class UsersService {
    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User createUser(User user) {
        Optional<User> foundUser = usersRepository.findByUsernameExactMatch(user.getUsername());
        Optional<User> foundUserEmail = usersRepository.findByEmailExactMatch(user.getEmail());

        if (foundUser.isPresent() || foundUserEmail.isPresent()) {
            throw new DuplicateUserException();
        }

        return usersRepository.save(user);

    }

    public void deleteUser(Long id) {
        Optional<User> user = usersRepository.findById(id);

        if (user.isPresent()) {
            usersRepository.delete(user.get());
        } else {
            throw new UserNotFoundException();
        }
    }

    public User updateUser(Long id, UserRequest updatedUser) {
        User user = getUser(id);
        
        if (user != null) {
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setEmail(updatedUser.getEmail());
            user.setCreditCard(updatedUser.getCreditCard());
            user.setVerified(updatedUser.isVerify());
            return usersRepository.save(user);
        }
        return null; 
    }

    public User getUser(Long id) {
        return usersRepository.findById(id).orElse(null);
    }

    public Boolean updateUserPassword(Long id, String oldPassword, String newPassword) {
        return null;
    }

    public User setAvatar(Long id, String url) {
        User user = getUser(id);

        if (user != null) {
            user.setAvatar(url);
            return usersRepository.save(user);
        }

        return null;
    }

    public UsersList searchUsers(String username) {
        if (username == null) username = "";

        UsersList users = new UsersList(usersRepository.findByUsername("%" + username + "%"));

        return users.isEmpty() ? null : users;
    }

}
