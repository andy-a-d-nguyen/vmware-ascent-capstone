package com.galvanize.useraccounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.useraccounts.UsersList;
import com.galvanize.useraccounts.exception.*;
import com.galvanize.useraccounts.model.Address;
import com.galvanize.useraccounts.model.User;
import com.galvanize.useraccounts.request.UserAvatarRequest;
import com.galvanize.useraccounts.request.UserPasswordRequest;
import com.galvanize.useraccounts.request.UserRequest;
import com.galvanize.useraccounts.service.AddressesService;
import com.galvanize.useraccounts.service.UsersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersController.class)
public class UsersControllerTests {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UsersService usersService;

    @MockBean
    AddressesService addressesService;

    ObjectMapper mapper = new ObjectMapper();

    @DisplayName("It can successfully create a user with valid attributes, status code 200 ok")
    @Test
    public void createUser() throws Exception {
        User userToAdd = new User("bakerBob", "password123", "bob","baker", "bakerBob@gmail.com");

        when(usersService.createUser(any(User.class))).thenReturn(userToAdd);
        //{"id":null,"username":"bakerBob","firstName":"bob","lastName":"baker","avatar":null,"email":"bakerBob@gmail.com",     "address":null,"creditCard":null,"verified":false}

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userToAdd)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("It should not create a user with invalid attributes, status code 400 bad request")
    @Test
    public void CreateUser_invalidAttr() throws Exception {
        when(usersService.createUser(any(User.class))).thenThrow(InvalidUserException.class);

        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("It should delete a user by id, status code 202 accepted ")
    @Test
    public void deleteUser_byId_acceptedStatusCode() throws Exception {
        User userToDelete = new User("bakerBob", "baker", "bob", "password123", "bakerBob@gmail.com");
        userToDelete.setId(1L);


        mockMvc.perform(delete("/api/users/" + userToDelete.getId()))
                .andExpect(status().isAccepted());

        verify(usersService).deleteUser(anyLong());
    }

    @DisplayName("It does not return anything if there are no users, status 204 no content")
    @Test
    public void deleteUser_byId_noContentStatusCode() throws Exception {
        doThrow(new UserNotFoundException()).when(usersService).deleteUser(anyLong());
        mockMvc.perform(delete("/api/users/1495"))
                .andExpect(status().isNoContent());
    }

    @DisplayName("It can successfully edit an existing account, status 200 ok")
    @Test
    public void editUser() throws Exception {
        User user = new User("bakerBob", "password123", "baker", "bob","bakerBob@gmail.com");

        when(usersService.updateUser(anyLong(), any(UserRequest.class))).thenReturn(user);

        mockMvc.perform(patch("/api/users/1234").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value("bakerBob"));
    }

    @DisplayName("It does not edit a user with invalid attributes, status 202 no content")
    @Test
    public void editUser_fails () throws Exception{
        User user = new User("bakerBob", "password123", "baker", "bob","bakerBob@gmail.com");

        when(usersService.updateUser(anyLong(), any(UserRequest.class))).thenReturn(null);

        mockMvc.perform(patch("/api/users/1234")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isNoContent());
    }

    @DisplayName("It should show user by id, status code 200 ok")
    @Test
    public void showUser_inputID_returnsUsers() throws Exception {
        User user = new User("bakerBob", "password123", "baker", "bob","bakerBob@gmail.com");
        user.setId(1L);

        when(usersService.getUser(anyLong())).thenReturn(user);

        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(user.getUsername()))
                .andExpect(jsonPath("firstName").value(user.getFirstName()))
                .andExpect(jsonPath("lastName").value(user.getLastName()))
                .andExpect(jsonPath("password").doesNotExist());
    }

    @DisplayName("It should not show a shower with an invalid id, status code 204 no content")
    @Test
    public void showUser_invalidID_returnsNoContent() throws Exception {

        when(usersService.getUser(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/users/1234"))
                .andExpect(status().isNoContent());
    }

    @DisplayName("It should successfully update a user's password, status code 200 ok")
    @Test
    public void updateUserPassword_success() throws Exception{
        UserPasswordRequest userPasswordRequest = new UserPasswordRequest("oldPassword", "newPassword");

        when(usersService.updateUserPassword(anyLong(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(patch("/api/users/1234/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userPasswordRequest)))
                .andExpect(status().isOk());
    }

    @DisplayName("It should not update a user's password, status code 202 no content")
    @Test
    public void updateUserPassword_failure() throws Exception{
        UserPasswordRequest userPasswordRequest = new UserPasswordRequest("oldPassword", "newPassword");

        when(usersService.updateUserPassword(anyLong(), anyString(), anyString())).thenReturn(false);

        mockMvc.perform(patch("/api/users/1234/reset").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(userPasswordRequest)))
                .andExpect(status().isNoContent());
    }

    @DisplayName("It should set avatar and return url, status code 200 ok")
    @Test
    public void setAvatar_InputIDAndAvatarURL_ReturnsURL() throws Exception{
        User user = new User("bakerBob", "password123", "baker", "bob","bakerBob@gmail.com");
        user.setId(1L);

        String avatar = "www.avatar.com";

        UserAvatarRequest request = new UserAvatarRequest(avatar);
        user.setAvatar(avatar);

        when(usersService.setAvatar(anyLong(), anyString())).thenReturn(user);

        mockMvc.perform(post("/api/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("avatar").value(avatar));
    }

    /********** Address ***********/

    @DisplayName("It should allow users to add an address, status code 200 ok")
    @Test
    public void createAddress_validAttr() throws Exception {
        Address newAddress = new Address("Test Street", "Test City","Test State", "Test Zipcode", "Test Apartment");
        newAddress.setUserId(1L);
        newAddress.setId(1L);
        when(addressesService.addAddress(anyLong(), any(Address.class))).thenReturn(newAddress);
        mockMvc.perform(post(String.format("/api/users/%d/addresses", 1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newAddress)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("userId").value("1"))
                .andExpect(jsonPath("city").value("Test City"))
                .andExpect(jsonPath("state").value("Test State"))
                .andExpect(jsonPath("zipcode").value("Test Zipcode"))
                .andExpect(jsonPath("apartment").value("Test Apartment"));
    }

    @DisplayName("It should throw an InvalidAddress error, status code 400 bad request")
    @Test
    public void addAddress_invalidAttr() throws Exception{
        when(addressesService.addAddress(anyLong(), any(Address.class))).thenThrow(InvalidAddressException.class);

        mockMvc.perform(post(String.format("/api/users/%d/addresses", 1L))
            .contentType(MediaType.APPLICATION_JSON)
            .content(""))
            .andExpect(status().isBadRequest());
    }

    @DisplayName("It should return a list of addresses, status code 200 ok")
    @Test
    public void getAddresses_success() throws Exception{
        List<Address> testAddresses = new ArrayList<>();

        IntStream.range(1, 6).forEach(num -> {
            Address newAddress = new Address("Test Street " + num, "Test City "+ num,"Test State "+ num, "Test Zipcode "+ num, "Test Apartment "+ num);
            newAddress.setUserId((long) num);
            newAddress.setId((long) num);
            testAddresses.add(newAddress);
        });

        when(addressesService.getAllAddresses(anyLong())).thenReturn(testAddresses);

        mockMvc.perform(get(String.format("/api/users/%d/addresses", 1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @DisplayName("It should not return content when there are no addresses associated with a user, status code 204 no content")
    @Test
    public void getAllAddresses_empty() throws Exception {
        when(addressesService.getAllAddresses(anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(get(String.format("/api/users/%d/addresses", 1L)))
                .andExpect(status().isNoContent());
    }

    @DisplayName("It should successfully edit a user's address, status code 200 ok")
    @Test
    public void updateAddress_success() throws Exception {
        Address updatedAddress = new Address("Test Street" , "Test City","Test State", "Test Zipcode", "Test Apartment");
        updatedAddress.setUserId(1L);
        updatedAddress.setId(1L);
        when(addressesService.updateAddress(anyLong(), any(Address.class))).thenReturn(updatedAddress);

        mockMvc.perform(patch(String.format("/api/users/%d/addresses", 1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedAddress)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("city").value("Test City"))
                .andExpect(jsonPath("state").value("Test State"))
                .andExpect(jsonPath("zipcode").value("Test Zipcode"))
                .andExpect(jsonPath("apartment").value("Test Apartment"));
    }
    @DisplayName("It fail to edit a user's address, status code 400 bad request")
    @Test
    public void updateAddress_fail() throws Exception {
        when(addressesService.updateAddress(anyLong(), any(Address.class))).thenThrow(InvalidAddressException.class);

        mockMvc.perform(patch(String.format("/api/users/%d/addresses", 1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("It should delete a user's address, status code 202 accepted")
    @Test
    public void deleteAddress() throws Exception {
        mockMvc.perform(delete("/api/users/1/addresses/1"))
                .andExpect(status().isAccepted());
        verify(addressesService).deleteAddress(1L, 1L);
    }

    @DisplayName("It should not return anything when there are no addresses, status code 204 no content")
    @Test
    void deleteAddress_notFound() throws Exception {
        doThrow(new AddressNotFoundException()).when(addressesService).deleteAddress(anyLong(), anyLong());
        mockMvc.perform(delete("/api/users/1/addresses/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void searchUsername_byString_returnsFoundUsers() throws Exception {
        String username = "bob";

        User user1 = new User("bakerBob", "password123", "baker", "bob","bakerBob1@gmail.com");
        User user2 = new User("bob", "password123", "bob", "smith","bakerBob2@gmail.com");
        User user3 = new User("bobBob", "password123", "bob", "bob","bakerBob3@gmail.com");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        when(usersService.searchUsers(anyString())).thenReturn(new UsersList(Arrays.asList(user1, user2, user3)));

        mockMvc.perform(get("/api/users?username=" + username))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("users", hasSize(3)));
    }

    @Test
    public void searchUsername_byString_returnsNoContent() throws Exception {
        String username = "bob";

        when(usersService.searchUsers(anyString())).thenReturn(new UsersList(Arrays.asList()));

        mockMvc.perform(get("/api/users?username=" + username))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void createUser_withDuplicateUsernameAndEmail_returnsError() throws Exception {
        User user = new User("bob", "password123", "bob", "smith","bakerBob2@gmail.com");

        when(usersService.createUser(any(User.class))).thenThrow(DuplicateUserException.class);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }
}
