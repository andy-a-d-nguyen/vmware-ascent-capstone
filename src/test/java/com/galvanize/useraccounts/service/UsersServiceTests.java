package com.galvanize.useraccounts.service;

import com.galvanize.useraccounts.exception.AddressNotFoundException;
import com.galvanize.useraccounts.exception.DuplicateUserException;
import com.galvanize.useraccounts.exception.InvalidAddressException;
import com.galvanize.useraccounts.exception.UserNotFoundException;
import com.galvanize.useraccounts.model.Address;
import com.galvanize.useraccounts.model.User;
import com.galvanize.useraccounts.repository.AddressRepository;
import com.galvanize.useraccounts.repository.UsersRepository;
import com.galvanize.useraccounts.request.UserPasswordRequest;
import com.galvanize.useraccounts.request.UserRequest;
import com.galvanize.useraccounts.service.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.galvanize.useraccounts.UsersList;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTests {
    @Mock
    UsersRepository usersRepository;
    @Mock
    AddressRepository addressRepository;

    private UsersService usersService;

    List<User> users;

    @BeforeEach
    void setup() {
        usersService = new UsersService(usersRepository, addressRepository);

        users = new ArrayList<>();

        User user1 = new User("bakerBob", "password123", "baker", "bob","bakerBob1@gmail.com");
        User user2 = new User("bob", "password123", "bob", "smith","bakerBob2@gmail.com");
        User user3 = new User("bobBob", "password123", "bob", "bob","bakerBob3@gmail.com");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        users.add(user1);
        users.add(user2);
        users.add(user3);
    }

    @Test
    void createUser_returnsUser() {
        when(usersRepository.save(any(User.class))).thenReturn(users.get(0));

        User actualUser = usersService.createUser(users.get(0));

        assertNotNull(actualUser);
        assertEquals(users.get(0), actualUser);
        assertEquals(users.get(0).getCreatedAt(), actualUser.getCreatedAt());
        assertEquals(users.get(0).getUpdatedAt(), actualUser.getUpdatedAt());
    }

    @Test
    void searchUsers_withString_returnsFoundUsers() {
        String username = "bob";

        when(usersRepository.findByUsername(anyString())).thenReturn(users);

        UsersList actual = usersService.searchUsers(username);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(users.size(), actual.size());
    }

    @Test
    void searchUsers_withString_returnsNoContent() {
        String username = "john";

        when(usersRepository.findByUsername(anyString())).thenReturn(new ArrayList<>());

        com.galvanize.useraccounts.UsersList actual = usersService.searchUsers(username);

        assertNull(actual);
    }

    @Test
    void createUser_withDuplicateUsername_throwsError() {
        User user4 = new User("bob", "password123", "bob", "smith","bakerBob2@gmail.com");

        when(usersRepository.save(any(User.class))).thenThrow(DuplicateUserException.class);

        assertThatExceptionOfType(DuplicateUserException.class).isThrownBy( () -> {
            usersService.createUser(user4);
        });
    }

    @Test
    void createUser_withDuplicateEmail_throwsError() {
        User user4 = new User("bob", "password123", "bob", "smith","bakerBob2@gmail.com");

        when(usersRepository.save(any(User.class))).thenThrow(DuplicateUserException.class);

        assertThatExceptionOfType(DuplicateUserException.class).isThrownBy( () -> {
            usersService.createUser(user4);
        });
    }

    @Test
    void deleteUser_withID_returnsAccepted() {
        User user = users.get(0);

        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(user));
        usersService.deleteUser(user.getId());

        verify(usersRepository).delete(any(User.class));
    }

    @Test
    void deleteUser_withID_throwsException() {
        when(usersRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    usersService.deleteUser(1231823L);
                });
    }

    @Test
    void getUser_withID_returnsUser() {
        User user = users.get(0);

        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User foundUser = usersService.getUser(user.getId());

        assertEquals(user, foundUser);
        assertEquals(user.getCreatedAt(), foundUser.getCreatedAt());
        assertEquals(user.getUpdatedAt(), foundUser.getUpdatedAt());
    }

    @Test
    void getUser_withID_returnsNoContent() {

        when(usersRepository.findById(anyLong())).thenReturn(Optional.empty());

        User foundUser = usersService.getUser(12345L);

        assertNull(foundUser);
    }

    @Test
    void updateUser_withIDAndBody_returnsUpdatedUser() {
        User user = users.get(0);
        user.setFirstName("Andy");
        user.setLastName("Nguyen");
        user.setEmail("andynguyen@gmail.com");

        UserRequest request = new UserRequest("Andy", "Nguyen", user.getPassword(), "andynguyen@gmail.com", user.getCreditCard(), user.isVerified(), user.getAvatar());

        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(usersRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = usersService.updateUser(user.getId(), request);

        assertEquals(user.getFirstName(), updatedUser.getFirstName());
        assertEquals(user.getLastName(), updatedUser.getLastName());
        assertEquals(user.getEmail(), updatedUser.getEmail());
        assertEquals(user.isVerified(), updatedUser.isVerified());
        assertEquals(user.getCreatedAt(), updatedUser.getCreatedAt());
        assertEquals(user.getUpdatedAt(), updatedUser.getUpdatedAt());
        assertEquals(user.getAvatar(), updatedUser.getAvatar());
    }

    @Test
    void updateUser_withIDAndBody_returnsNoContent() {
        User user = users.get(0);

        UserRequest request = new UserRequest("Andy", "Nguyen", user.getPassword(), "andynguyen@gmail.com", user.getCreditCard(), user.isVerified(), user.getAvatar());

        when(usersRepository.findById(anyLong())).thenReturn(Optional.empty());

        User updatedUser = usersService.updateUser(1234L, request);

        assertNull(updatedUser);
    }

    @Test
    void updatePassword_withIDAndRequestBody_returnsTrue() {
        User user = users.get(0);

        UserPasswordRequest passwordRequest = new UserPasswordRequest("password123", "newpassword");

        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Boolean updatedPassword = usersService.updateUserPassword(user.getId(), passwordRequest.getOldPassword(), passwordRequest.getNewPassword());

        assertTrue(updatedPassword);
    }

    @Test
    void updatePassword_withIDAndRequestBody_returnsFalse() {
        UserPasswordRequest passwordRequest = new UserPasswordRequest("password123", "newpassword");

        when(usersRepository.findById(anyLong())).thenReturn(Optional.empty());

        Boolean updatedPassword = usersService.updateUserPassword(1234L, passwordRequest.getOldPassword(), passwordRequest.getNewPassword());

        assertFalse(updatedPassword);
    }

    @DisplayName("It should save a user's address and returns updated user")
    @Test
    void addAddress_to_User(){
        User expected = new User("username", "password123", "John", "Smith", "jsmith@gmail.com");
        Address address = new Address("StreetName", "Honolulu", "Hawaii", "21343-343", null, null);
        expected.setId(1L);
        expected.addAddress(address);

//        List<Address> addressList = new ArrayList<>();
//        addressList.add(address);

        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(expected));
        when(usersRepository.save(any(User.class))).thenReturn(expected);

        User actual = usersService.addAddress(1L, address);
        assertEquals(expected.getAddresses().size(), actual.getAddresses().size());
        assertEquals(users.get(0).getUpdatedAt(), actual.getUpdatedAt());
    }

    @DisplayName("It should throw UserNotFoundException when adding an address to non existing user")
    @Test
    void addAddresses_to_NonExistingUser(){
        when(usersRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    usersService.addAddress(1L, new Address());
                });
    }

    @DisplayName("It should save addresses to a user during creation and return user")
    @Test
    void addAddresses_to_User(){
        User expected = new User("username", "password123", "John", "Smith", "jsmith@gmail.com");
        Address address = new Address("StreetName", "Honolulu", "Hawaii", "21343-343", "#22", "home");

        expected.addAddress(address);

        List<Address> addressList = new ArrayList<>();
        addressList.add(address);

        when(usersRepository.save(any(User.class))).thenReturn(expected);

        User aUser = new User("username", "password123", "John", "Smith", "jsmith@gmail.com");
        aUser.addAddress(address);
        User actual = usersService.createUser(aUser);

        assertEquals(expected.getAddresses().size(), actual.getAddresses().size());

        assertEquals(expected.getAddresses().get(0).getStreet(), actual.getAddresses().get(0).getStreet());
        assertEquals(expected.getAddresses().get(0).getCity(), actual.getAddresses().get(0).getCity());
        assertEquals(expected.getAddresses().get(0).getState(), actual.getAddresses().get(0).getState());
        assertEquals(expected.getAddresses().get(0).getZipcode(), actual.getAddresses().get(0).getZipcode());
        assertEquals(expected.getAddresses().get(0).getApartment(), actual.getAddresses().get(0).getApartment());
        assertEquals(expected.getAddresses().get(0).getLabel(), actual.getAddresses().get(0).getLabel());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt());
    }

    @DisplayName("It should update the address of an user that exists")
    @Test
    void updateAddress_success(){
        User expected = new User("username", "password123", "John", "Smith", "jsmith@gmail.com");
        expected.setId(1L);
        Address address = new Address("StreetName", "Honolulu", "Hawaii", "21343-343", null, null);
        address.setId(1L);
        expected.addAddress(address);

        List<Address> addressList = new ArrayList<>();
        addressList.add(address);

        Address updatedAddress = new Address("Updated", "Miami", "Ohio", "dk3j4323", null, null);
        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(expected));
        when(usersRepository.save(any(User.class))).thenReturn(expected);


        User actual2 = usersService.updateAddress(1L, 1L, address);

        assertEquals(expected.getCreatedAt(), actual2.getCreatedAt());
        assertEquals(expected.getUpdatedAt(), actual2.getUpdatedAt());

        User actual = usersService.updateAddress(1L, 1L, updatedAddress);
        assertEquals(expected.getAddresses().get(0).getStreet(), actual.getAddresses().get(0).getStreet());
        assertEquals(expected.getAddresses().get(0).getState(), actual.getAddresses().get(0).getState());
        assertEquals(expected.getAddresses().get(0).getCity(), actual.getAddresses().get(0).getCity());
        assertEquals(expected.getAddresses().get(0).getZipcode(), actual.getAddresses().get(0).getZipcode());
        assertNotEquals(expected.getAddresses().get(0).getStreet(), "StreetName");
        assertNotEquals(expected.getAddresses().get(0).getState(), "Honolulu");
        assertNotEquals(expected.getAddresses().get(0).getCity(), "Hawaii");
        assertNotEquals(expected.getAddresses().get(0).getZipcode(), "21343-343");
    }

    @DisplayName("It fail to update the address of an user that does not exist")
    @Test
    void updateAddress_fails_noUserFound(){
        Address address = new Address("StreetName", "Honolulu", "Hawaii", "21343-343", null, null);
        address.setId(1L);

        when(usersRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    usersService.updateAddress(1L, 1L, address);
                });
    }

    @DisplayName("It fail to update the address of an user when the aforementioned address does not exist")
    @Test
    void updateAddress_fails_noAddressFound(){
        User expected = new User("username", "password123", "John", "Smith", "jsmith@gmail.com");
        expected.setId(1L);
        Address address = new Address("StreetName", "Honolulu", "Hawaii", "21343-343", null, null);
        address.setId(1L);
        expected.addAddress(address);

        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(expected));
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatExceptionOfType(AddressNotFoundException.class)
                .isThrownBy(() -> {
                    usersService.updateAddress(1L, 18L, address);
                });
    }

    @DisplayName("It should delete the address of an user that exists")
    @Test
    void deleteAddress_success(){
        User expected = new User("username", "password123", "John", "Smith", "jsmith@gmail.com");
        expected.setId(1L);

        User actual = new User("actual", "password123", "John", "Smith", "jsmith@gmail.com");
        actual.setId(2L);

        Address address = new Address("StreetName", "Honolulu", "Hawaii", "21343-343", null, null);
        address.setId(1L);
        actual.addAddress(address);

        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(actual));
        when(addressRepository.findById(anyLong())).thenReturn(Optional.of(address));
        doNothing().when(addressRepository).delete(address);
        usersService.deleteAddress(2L, 1L);

        assertEquals(expected.getAddresses().size(), actual.getAddresses().size());
        verify(addressRepository).delete(address);

        expected.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        expected.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        actual.setCreatedAt(expected.getCreatedAt());
        actual.setUpdatedAt(expected.getUpdatedAt());

        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt());
    }

    @DisplayName("It should fail to delete the address of an user that does not exist")
    @Test
    void deleteAddress_fail_noUser(){

        when(usersRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    usersService.deleteAddress(1L, 18L);
                });
    }

    @DisplayName("It should fail to delete an address that doesn't exist of an user")
    @Test
    void deleteAddress_fail_noAddress(){
        User user = new User("user", "password123", "John", "Smith", "jsmith@gmail.com");
        user.setId(2L);

        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatExceptionOfType(AddressNotFoundException.class)
                .isThrownBy(() -> {
                    usersService.deleteAddress(1L, 18L);
                });
    }
}