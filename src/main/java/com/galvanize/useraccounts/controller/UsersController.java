package com.galvanize.useraccounts.controller;

import com.galvanize.useraccounts.UsersList;
import com.galvanize.useraccounts.exception.*;
import com.galvanize.useraccounts.model.Address;
import com.galvanize.useraccounts.request.UserAvatarRequest;
import com.galvanize.useraccounts.service.AddressesService;
import com.galvanize.useraccounts.service.UsersService;
import com.galvanize.useraccounts.model.User;
import com.galvanize.useraccounts.request.UserPasswordRequest;
import com.galvanize.useraccounts.request.UserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UsersController {
    UsersService usersService;
    AddressesService addressesService;

    public UsersController(UsersService usersService, AddressesService addressesService) {
        this.usersService = usersService;
        this.addressesService = addressesService;
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) throws InvalidUserException, DuplicateUserException, InvalidAddressException {
        return usersService.createUser(user);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/users/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody UserRequest updatedUser) throws InvalidUserException {
        User user = usersService.updateUser(id, updatedUser);

        return user == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/users/{id}/reset")
    public ResponseEntity<Boolean> update(@PathVariable Long id, @RequestBody UserPasswordRequest updatedUserPassword) throws InvalidUserException {
        Boolean isUpdated = usersService.updateUserPassword(id, updatedUserPassword.getOldPassword(), updatedUserPassword.getNewPassword());

        return !isUpdated ? ResponseEntity.noContent().build() : ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        try {
            usersService.deleteUser(id);
        } catch(UserNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.accepted().build();

    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/users/{id}")
    public ResponseEntity<User> setAvatar(@PathVariable Long id, @RequestBody UserAvatarRequest userAvatarRequest) {
        User user =  usersService.setAvatar(id, userAvatarRequest.getUrl());
        
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = usersService.getUser(id);
        return user == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(user);
    }

    /*Addresses*/

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/users/{userId}/addresses")
    public User createAddress(@PathVariable Long userId, @Validated @RequestBody Address address) {
           return usersService.addAddress(userId, address);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/users/{userId}/addresses/{addressId}")
    public ResponseEntity<User> updateAddress (@PathVariable Long userId, @PathVariable Long addressId, @Valid @RequestBody Address address) throws UserNotFoundException, InvalidAddressException {
        User updatedUser = usersService.updateAddress(userId, addressId, address);
        return updatedUser == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/users/{userId}/addresses/{addressId}")
    public ResponseEntity deleteAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        try {
          usersService.deleteAddress(userId, addressId);
        } catch(AddressNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.accepted().build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<UsersList> searchUsers(@RequestParam(required = false) String username) {
        UsersList users = usersService.searchUsers(username);

        return users == null || users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }


    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void userNotFoundException(UserNotFoundException exception) {
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addressNotFoundException(AddressNotFoundException exception) {
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void invalidAddressException(InvalidAddressException exception) {
    }

}
