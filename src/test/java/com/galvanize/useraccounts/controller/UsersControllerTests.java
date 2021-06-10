package com.galvanize.useraccounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.useraccounts.exception.AddressNotFoundException;
import com.galvanize.useraccounts.exception.InvalidAddressException;
import com.galvanize.useraccounts.exception.InvalidUserException;
import com.galvanize.useraccounts.exception.UserNotFoundException;
import com.galvanize.useraccounts.model.Address;
import com.galvanize.useraccounts.model.User;
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

@WebMvcTest
public class UsersControllerTests {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UsersService usersService;

    @MockBean
    AddressesService addressesService;

    ObjectMapper mapper = new ObjectMapper();

    @DisplayName("It can successfully create a user with valid attributes with a status of 200 OK")
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

    @DisplayName("It should not create a user with invalid attributes and return a Bad Request 400")
    @Test
    public void CreateUser_invalidAttr() throws Exception {
        when(usersService.createUser(any(User.class))).thenThrow(InvalidUserException.class);

        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteUser_byId_acceptedStatusCode() throws Exception {
        User userToDelete = new User("bakerBob", "baker", "bob", "password123", "bakerBob@gmail.com");
        userToDelete.setId(new Long(1));


        mockMvc.perform(delete("/api/users/" + userToDelete.getId()))
                .andExpect(status().isAccepted());

        verify(usersService).deleteUser(anyLong());
    }

    @Test
    public void deleteUser_byId_noContentStatusCode() throws Exception {
        doThrow(new UserNotFoundException()).when(usersService).deleteUser(anyLong());
        mockMvc.perform(delete("/api/users/1495"))
                .andExpect(status().isNoContent());
    }

    @DisplayName("It can successfully edit an existing account with a status of 200 OK")
    @Test
    public void editUser() throws Exception {
        User user = new User("bakerBob", "password123", "baker", "bob","bakerBob@gmail.com");

        when(usersService.updateUser(anyLong(), any(UserRequest.class))).thenReturn(user);

        mockMvc.perform(patch("/api/users/1234").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value("bakerBob"));
    }

    @DisplayName("It does not edit a user with invalid attributes, returns status of 202 no content")
    @Test
    public void editUser_fails () throws Exception{
        User user = new User("bakerBob", "password123", "baker", "bob","bakerBob@gmail.com");

        when(usersService.updateUser(anyLong(), any(UserRequest.class))).thenReturn(null);

        mockMvc.perform(patch("/api/users/1234")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isNoContent());
    }

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

    @Test
    public void showUser_invalidID_returnsNoContent() throws Exception {

        when(usersService.getUser(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/users/1234"))
                .andExpect(status().isNoContent());
    }

    @DisplayName("It should successfully update a user's password, returns status of 200 OK")
    @Test
    public void updateUserPassword_success() throws Exception{
        UserPasswordRequest userPasswordRequest = new UserPasswordRequest("oldPassword", "newPassword");

        when(usersService.updateUserPassword(anyLong(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(patch("/api/users/1234/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userPasswordRequest)))
                .andExpect(status().isOk());
    }

    @DisplayName("It should not update a user's password and return a 202 NO CONTENT")
    @Test
    public void updateUserPassword_failure() throws Exception{
        UserPasswordRequest userPasswordRequest = new UserPasswordRequest("oldPassword", "newPassword");

        when(usersService.updateUserPassword(anyLong(), anyString(), anyString())).thenReturn(false);

        mockMvc.perform(patch("/api/users/1234/reset").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(userPasswordRequest)))
                .andExpect(status().isNoContent());
    }

    /********** Address ***********/

    @DisplayName("It should allow user's to add an address and return 200 OK")
    @Test
    public void createAddress_validAttr() throws Exception {
        Address newAddress = new Address("Test Street", "Test City","Test State", "Test Zipcode", "Test Apartment");
        newAddress.setUserId(1);
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

    @DisplayName("It should throw an InvalidAddress error, status code 400")
    @Test
    public void addAddress_invalidAttr() throws Exception{
        when(addressesService.addAddress(anyLong(), any(Address.class))).thenThrow(InvalidAddressException.class);

        mockMvc.perform(post(String.format("/api/users/%d/addresses", 1L))
            .contentType(MediaType.APPLICATION_JSON)
            .content(""))
            .andExpect(status().isBadRequest());
    }

    @DisplayName("It should return a list of addresses, status code 200")
    @Test
    public void getAddresses_success() throws Exception{
        List<Address> testAddresses = new ArrayList<>();

        IntStream.range(1, 6).forEach(num -> {
            Address newAddress = new Address("Test Street " + num, "Test City "+ num,"Test State "+ num, "Test Zipcode "+ num, "Test Apartment "+ num);
            newAddress.setUserId(num);
            testAddresses.add(newAddress);
        });

        when(addressesService.getAllAddresses(anyLong())).thenReturn(testAddresses);

        mockMvc.perform(get(String.format("/api/users/%d/addresses", 1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @DisplayName("It should return a 204, no content when there are no addresses associated with a user")
    @Test
    public void getAllAddresses_empty() throws Exception {
        when(addressesService.getAllAddresses(anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(get(String.format("/api/users/%d/addresses", 1L)))
                .andExpect(status().isNoContent());
    }

    @DisplayName("It should successfully edit a user's address, code 200")
    @Test
    public void updateAddress_success() throws Exception {
        Address updatedAddress = new Address("Test Street" , "Test City","Test State", "Test Zipcode", "Test Apartment");

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
    @DisplayName("It fail to edit a user's address, code 400")
    @Test
    public void updateAddress_fail() throws Exception {

        Address updatedAddress = new Address("Test Street" , "Test City","Test State", "Test Zipcode", "Test Apartment");

        when(addressesService.updateAddress(anyLong(), any(Address.class))).thenThrow(InvalidAddressException.class);

        mockMvc.perform(patch(String.format("/api/users/%d/addresses", 1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("It should return a 202 Accepted when deleting a user's address")
    @Test
    public void deleteAddress() throws Exception {
        mockMvc.perform(delete("/api/users/1/addresses/1"))
                .andExpect(status().isAccepted());
        verify(addressesService).deleteAddress(1L, 1L);
    }

    @DisplayName("It should return a status code of 204 when no address is found in the database")
    @Test
    void deleteAddress_notFound() throws Exception {
        doThrow(new AddressNotFoundException()).when(addressesService).deleteAddress(anyLong(), anyLong());
        mockMvc.perform(delete("/api/users/1/addresses/1"))
                .andExpect(status().isNoContent());
    }
}
