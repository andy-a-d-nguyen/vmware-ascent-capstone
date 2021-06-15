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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public User createUser(@Valid @RequestBody User user) throws InvalidUserException, DuplicateUserException {
        return usersService.createUser(user);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody UserRequest updatedUser) throws InvalidUserException {
        User user = usersService.updateUser(id, updatedUser);

        return user == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(user);
    }

    @PatchMapping("/users/{id}/reset")
    public ResponseEntity<Boolean> update(@PathVariable Long id, @RequestBody UserPasswordRequest updatedUserPassword) throws InvalidUserException {
        Boolean isUpdated = usersService.updateUserPassword(id, updatedUserPassword.getOldPassword(), updatedUserPassword.getNewPassword());

        return !isUpdated ? ResponseEntity.noContent().build() : ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        try {
            usersService.deleteUser(id);
        } catch(UserNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.accepted().build();

    }
    @PostMapping("/users/{id}")
    public ResponseEntity<User> setAvatar(@PathVariable Long id, @RequestBody UserAvatarRequest userAvatarRequest) {
        User user =  usersService.setAvatar(id, userAvatarRequest.getUrl());
        
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = usersService.getUser(id);
        return user == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(user);
    }

    /*Addresses*/

    @PostMapping("/users/{userId}/addresses")
    public User createAddress(@PathVariable Long userId, @Valid @RequestBody List<Address> address) throws InvalidAddressException {
       return usersService.addAddress(userId, address);
    }

    @GetMapping("/users/{userId}/addresses")
    public ResponseEntity< List<Address> > getAddresses(@PathVariable Long userId){
        List<Address> addresses = addressesService.getAllAddresses(userId);
        return addresses.size() > 0 ?  ResponseEntity.ok(addresses) : ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{userId}/addresses")
    public ResponseEntity<Address> updateAddress (@PathVariable Long userId, @Valid @RequestBody Address address){
        Address updatedAddress = addressesService.updateAddress(userId, address);
        return updatedAddress == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/users/{userId}/addresses/{addressId}")
    public ResponseEntity deleteAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        try {
            //addressesService.deleteAddress(userId, addressId);
        } catch(AddressNotFoundException e) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/users")
    public ResponseEntity<UsersList> searchUsers(@RequestParam(required = false) String username) {
        UsersList users = usersService.searchUsers(username);

        return users == null || users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }

}
