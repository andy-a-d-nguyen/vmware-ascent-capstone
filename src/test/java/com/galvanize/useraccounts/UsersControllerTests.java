package com.galvanize.useraccounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
    ObjectMapper mapper = new ObjectMapper();

    @DisplayName("It can successfully create a user with valid attributes with a status of 200 OK")
    @Test
    public void createUser() throws Exception {
        User userToAdd = new User("bakerBob", "baker", "bob","password123", "bakerBob@gmail.com");
        when(usersService.createUser(any(User.class))).thenReturn(userToAdd);

        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(userToAdd)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value("bakerBob"));
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
        User userToDelete = new User("bakerBob", "baker", "bob","password123", "bakerBob@gmail.com");

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

        when(usersService.updateUser(anyLong(), any(UpdateUserRequest.class))).thenReturn(user);

        mockMvc.perform(patch("/api/users/1234").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value("bakerBob"));
    }

    @DisplayName("It does not edit a user with invalid attributes, returns status of 202 no content")
    @Test
    public void editUser_fails () throws Exception{
        User user = new User("bakerBob", "password123", "baker", "bob","bakerBob@gmail.com");

        when(usersService.updateUser(anyLong(), any(UpdateUserRequest.class))).thenReturn(null);

        mockMvc.perform(patch("/api/users/1234").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(user)))
                .andExpect(status().isNoContent());

    }

}
