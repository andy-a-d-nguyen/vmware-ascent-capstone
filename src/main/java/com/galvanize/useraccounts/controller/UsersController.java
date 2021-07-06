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
import java.util.Optional;

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
    public User createUser(@Valid @RequestBody User user, @AuthenticationPrincipal JwtUser jwtUser) throws InvalidUserException, DuplicateUserException, InvalidAddressException {
        // check whether guid already exists
        Long jwtGuid = jwtUser.getGuid();
        User foundUser = usersService.getUser(jwtGuid);
        if (foundUser == null) {
            return usersService.createUser(user);
        } else {
            throw new DuplicateUserException();
        }
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/users/{guid}")
    public ResponseEntity<User> update(@PathVariable Long guid, @RequestBody UserRequest updatedUser, @AuthenticationPrincipal JwtUser jwtUser) throws InvalidUserException, DuplicateEmailException {
        // get guid from token
        Long jwtGuid = jwtUser.getGuid();
        Optional<User> oFoundUser;

        // get guid from database
        User updatedUserReturned = null;
        // compare jwt user with user's guid
        if (jwtGuid.equals(guid)) {
            oFoundUser = usersService.searchByEmail(updatedUser.getEmail());

            if (oFoundUser.isPresent() && jwtUser.getUsername() != oFoundUser.get().getUsername() && updatedUser.getEmail() == oFoundUser.get().getEmail()) {
                throw new DuplicateEmailException();
            } else {
                updatedUserReturned = usersService.updateUser(guid, updatedUser);
            }

        } else {
            // not same, invalid
            throw new UserNotFoundException();
        }

        return updatedUserReturned == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(updatedUserReturned);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/users/{guid}")
    public ResponseEntity deleteUser(@PathVariable Long guid, @AuthenticationPrincipal JwtUser jwtUser) {
        Long jwtGuid = jwtUser.getGuid();

        if (jwtGuid.equals(guid) && usersService.getUser(jwtGuid) != null) {
            usersService.deleteUser(guid);
        } else {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.accepted().build();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/users/{guid}")
    public ResponseEntity<User> getUser(@PathVariable Long guid, @AuthenticationPrincipal JwtUser jwtUser) {
        Long jwtGuid = jwtUser.getGuid();

        User user = null;

        if (jwtGuid.equals(guid)) {
            user = usersService.getUser(guid);
            if (user == null) return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(user);
    }

    /*Addresses*/

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/users/{guid}/addresses")
    public User createAddress(@PathVariable Long guid, @Validated @RequestBody Address address, @AuthenticationPrincipal JwtUser jwtUser) {
        Long jwtGuid = jwtUser.getGuid();

        if (jwtGuid.equals(guid)) return usersService.addAddress(guid, address);
        else return null;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/users/{guid}/addresses/{addressId}")
    public ResponseEntity<User> updateAddress(@PathVariable Long guid, @PathVariable Long addressId, @Valid @RequestBody Address address,
                                              @AuthenticationPrincipal JwtUser jwtUser) throws UserNotFoundException, InvalidAddressException, AddressNotFoundException {
        Long jwtGuid = jwtUser.getGuid();

        User updatedUser = null;

        if (jwtGuid.equals(guid)) updatedUser = usersService.updateAddress(guid, addressId, address);

        if (updatedUser == null) throw new UserNotFoundException();
        else return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/users/{guid}/addresses/{addressId}")
    public ResponseEntity deleteAddress(@PathVariable Long guid, @PathVariable Long addressId, @AuthenticationPrincipal JwtUser jwtUser) {
        Long jwtGuid = jwtUser.getGuid();

        if (jwtGuid.equals(guid) && usersService.getUser(jwtGuid) != null) {
            usersService.deleteAddress(guid, addressId);
        } else {
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

    @GetMapping("/users/{guid}/condensed")
    public ResponseEntity<UserCondensed> getUserCondensed(@PathVariable Long guid) {
        UserCondensed userCondensed;
        userCondensed = usersService.getUserCondensed(guid);

        if (userCondensed == null) throw new UserNotFoundException();
        else return ResponseEntity.ok(userCondensed);
    }

}
