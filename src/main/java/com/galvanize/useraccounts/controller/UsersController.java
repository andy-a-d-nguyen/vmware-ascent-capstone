package com.galvanize.useraccounts.controller;

import com.galvanize.useraccounts.UsersList;
import com.galvanize.useraccounts.exception.*;
import com.galvanize.useraccounts.model.Address;
import com.galvanize.useraccounts.model.UserCondensed;
import com.galvanize.useraccounts.request.UserAvatarRequest;
import com.galvanize.useraccounts.security.JwtUser;
import com.galvanize.useraccounts.service.AddressesService;
import com.galvanize.useraccounts.service.UsersService;
import com.galvanize.useraccounts.model.User;
import com.galvanize.useraccounts.request.UserPasswordRequest;
import com.galvanize.useraccounts.request.UserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class UsersController {
    UsersService usersService;
    AddressesService addressesService;

    public UsersController(UsersService usersService, AddressesService addressesService) {
        this.usersService = usersService;
        this.addressesService = addressesService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) throws InvalidUserException, DuplicateUserException, InvalidAddressException {
        return usersService.createUser(user);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/users/{guid}")
    public ResponseEntity<User> update(@PathVariable Long guid, @RequestBody UserRequest updatedUser, @AuthenticationPrincipal JwtUser jwtUser) throws InvalidUserException {
        User user = usersService.updateUser(guid, updatedUser);
        // compare jwt user with user's guid
        // not same, invalid

        return user == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(user);
    }


    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/users/{guid}")
    public ResponseEntity deleteUser(@PathVariable Long guid) {
        try {
            usersService.deleteUser(guid);
        } catch (UserNotFoundException e) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.accepted().build();

    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/users/{guid}")
    public ResponseEntity<User> getUser(@PathVariable Long guid) {
        User user = usersService.getUser(guid);
        return user == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(user);
    }

    /*Addresses*/

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/users/{guid}/addresses")
    public User createAddress(@PathVariable Long guid, @Validated @RequestBody Address address) {
        return usersService.addAddress(guid, address);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/users/{guid}/addresses/{addressId}")
    public ResponseEntity<User> updateAddress(@PathVariable Long guid, @PathVariable Long addressId, @Valid @RequestBody Address address) throws UserNotFoundException, InvalidAddressException {
        User updatedUser = usersService.updateAddress(guid, addressId, address);
        return updatedUser == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/users/{guid}/addresses/{addressId}")
    public ResponseEntity deleteAddress(@PathVariable Long guid, @PathVariable Long addressId) {
        try {
            usersService.deleteAddress(guid, addressId);
        } catch (AddressNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.accepted().build();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/users")
    public ResponseEntity<UsersList> searchUsers(@RequestParam(required = false) String username) {
        UsersList users = usersService.searchUsers(username);

        return users == null || users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/users/{guid}/condensed")
    public ResponseEntity<UserCondensed> getUserCondensed(@PathVariable Long guid) {
        UserCondensed user = usersService.getUserCondensed(guid);
        return user == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(user);
    }

}
