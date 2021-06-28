package com.galvanize.useraccounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.useraccounts.UsersList;
import com.galvanize.useraccounts.exception.*;
import com.galvanize.useraccounts.model.Address;
import com.galvanize.useraccounts.model.User;
import com.galvanize.useraccounts.model.UserCondensed;
import com.galvanize.useraccounts.request.UserAvatarRequest;
import com.galvanize.useraccounts.request.UserPasswordRequest;
import com.galvanize.useraccounts.request.UserRequest;
import com.galvanize.useraccounts.service.AddressesService;
import com.galvanize.useraccounts.service.UsersService;
import com.jayway.jsonpath.JsonPath;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersController.class)
@TestPropertySource(locations="classpath:application-test.properties")
public class UsersControllerTests {
    @Value("${security.jwt.secret}")
    String JWT_KEY;
    String token;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UsersService usersService;

    @MockBean
    AddressesService addressesService;

    private User user;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        user = new User("bakerBob", "password123", "bob","baker", "bakerBob@gmail.com");
        user.setId(1L);

        token = getToken("user", Arrays.asList("ROLE_USER"));
    }

    private String getToken(String username, List<String> roles) {
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .claim("name", username)
                .claim("guid", 99)
                // Convert to list of strings.
                // This is important because it affects the way we get them back in the Gateway.
                .claim("authorities", roles)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 5256000 * 1000L))  // in milliseconds
                .signWith(SignatureAlgorithm.HS512, JWT_KEY.getBytes())
                .compact();

        return String.format("Bearer %s", token);
    }

    @DisplayName("It can successfully create a user with valid attributes, status code 200 ok")
    @Test
    public void createUser() throws Exception {
        User userToAdd = new User("bakerBob", "password123", "bob","baker", "bakerBob@gmail.com");
        userToAdd.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        userToAdd.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        when(usersService.createUser(any(User.class))).thenReturn(userToAdd);
        //{"id":null,"username":"bakerBob","firstName":"bob","lastName":"baker","avatar":null,"email":"bakerBob@gmail.com",     "address":null,"creditCard":null,"verified":false}

        mockMvc.perform(post("/api/users").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userToAdd)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("updatedAt").exists());
    }

    @DisplayName("It should not create a user with invalid attributes, status code 400 bad request")
    @Test
    public void CreateUser_invalidAttr() throws Exception {
        when(usersService.createUser(any(User.class))).thenThrow(InvalidUserException.class);

        mockMvc.perform(post("/api/users").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("It should delete a user by id, status code 202 accepted ")
    @Test
    public void deleteUser_byId_acceptedStatusCode() throws Exception {
        User userToDelete = new User("bakerBob", "baker", "bob", "password123", "bakerBob@gmail.com");
        userToDelete.setId(1L);


        mockMvc.perform(delete("/api/users/" + userToDelete.getId()).header("Authorization", token))
                .andExpect(status().isAccepted());

        verify(usersService).deleteUser(anyLong());
    }

    @DisplayName("It does not return anything if there are no users, status 204 no content")
    @Test
    public void deleteUser_byId_noContentStatusCode() throws Exception {
        doThrow(new UserNotFoundException()).when(usersService).deleteUser(anyLong());
        mockMvc.perform(delete("/api/users/1495").header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @DisplayName("It can successfully edit an existing account, status 200 ok")
    @Test
    public void editUser() throws Exception {
        User user = new User("bakerBob", "password123", "baker", "bob","bakerBob@gmail.com");
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        when(usersService.updateUser(anyLong(), any(UserRequest.class))).thenReturn(user);

        mockMvc.perform(patch("/api/users/1234").header("Authorization", token).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value("bakerBob"))
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("updatedAt").exists());
    }

    @DisplayName("It does not edit a user with invalid attributes, status 202 no content")
    @Test
    public void editUser_fails () throws Exception{
        User user = new User("bakerBob", "password123", "baker", "bob","bakerBob@gmail.com");

        when(usersService.updateUser(anyLong(), any(UserRequest.class))).thenReturn(null);

        mockMvc.perform(patch("/api/users/1234").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isNoContent());
    }

    @DisplayName("It should show user by id, status code 200 ok")
    @Test
    public void showUser_inputID_returnsUsers() throws Exception {
        User user = new User("bakerBob", "password123", "baker", "bob","bakerBob@gmail.com");
        user.setId(1L);
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        when(usersService.getUser(anyLong())).thenReturn(user);

        mockMvc.perform(get("/api/users/" + user.getId()).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(user.getUsername()))
                .andExpect(jsonPath("firstName").value(user.getFirstName()))
                .andExpect(jsonPath("lastName").value(user.getLastName()))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("updatedAt").exists());
    }

    @DisplayName("It should not show a shower with an invalid id, status code 204 no content")
    @Test
    public void showUser_invalidID_returnsNoContent() throws Exception {

        when(usersService.getUser(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/users/1234").header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @DisplayName("It should successfully update a user's password, status code 200 ok")
    @Test
    public void updateUserPassword_success() throws Exception{
        UserPasswordRequest userPasswordRequest = new UserPasswordRequest("oldPassword", "newPassword");

        when(usersService.updateUserPassword(anyLong(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(patch("/api/users/1234/reset").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userPasswordRequest)))
                .andExpect(status().isOk());
    }

    @DisplayName("It should not update a user's password, status code 202 no content")
    @Test
    public void updateUserPassword_failure() throws Exception{
        UserPasswordRequest userPasswordRequest = new UserPasswordRequest("oldPassword", "newPassword");

        when(usersService.updateUserPassword(anyLong(), anyString(), anyString())).thenReturn(false);

        mockMvc.perform(patch("/api/users/1234/reset").header("Authorization", token).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(userPasswordRequest)))
                .andExpect(status().isNoContent());
    }

    /********** Address ***********/

    // Test GET for returned addresses


    @DisplayName("It should allow users to add addresses when creating an account , status code 200 ok")
    @Test
    public void createUserWithAddresses() throws Exception {
        Address newAddress = new Address("Test Street", "Test City","Test State", "Test Zipcode", "Test Apartment", null);
        user.addAddress(newAddress);
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        newAddress.setId(1L);
        when(usersService.createUser(any(User.class))).thenReturn(user);

        String content = "{\"username\":\"TestUsername3\",\"firstName\":\"First3\",\"lastName\":\"Last3\",\"password\":\"password\",\"email\":\"email3@email.com\",\"addresses\":[{\"street\":\"test street\",\"state\":\"test state\",\"city\":\"test city\",\"zipcode\":\"00000\"},{\"street\":\"test street2\",\"state\":\"test state2\",\"city\":\"test city2\",\"zipcode\":\"00000\"}]}";
        mockMvc.perform(post("/api/users").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("addresses", hasSize(1)))
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("updatedAt").exists());
    }

    @DisplayName("It should allow existing users to add an address , status code 200 ok")
    @Test
    public void createAddressForExistingUser() throws Exception {
        Address newAddress = new Address("Test Street", "Test City","Test State", "Test Zipcode", "Test Apartment", null);
        user.addAddress(newAddress);
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        newAddress.setId(1L);
        when(usersService.addAddress(anyLong(), any(Address.class))).thenReturn(user);
        mockMvc.perform(post(String.format("/api/users/%d/addresses", 1L)).header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newAddress)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("addresses", hasSize(1)))
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("updatedAt").exists());
    }



    @DisplayName("It should throw an InvalidAddress error, status code 400 bad request")
    @Test
    public void addAddress_invalidAttr() throws Exception{
        when(usersService.addAddress(anyLong(), any(Address.class))).thenThrow(InvalidAddressException.class);

        mockMvc.perform(post(String.format("/api/users/%d/addresses", 1L)).header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(""))
            .andExpect(status().isBadRequest());
    }



    @DisplayName("It should successfully edit a user's address, status code 200 ok")
    @Test
    public void updateAddress_success() throws Exception {
        Address updatedAddress = new Address("Test Street" , "Test City","Test State", "Test Zipcode", "Test Apartment", null);
        updatedAddress.setId(1L);
        user.addAddress(updatedAddress);
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        when(usersService.getUser(anyLong())).thenReturn(user);
        when(usersService.updateAddress(anyLong(), anyLong(), any(Address.class))).thenReturn(user);

        MvcResult result = mockMvc.perform(patch(String.format("/api/users/%d/addresses/%d", 1L, 1L)).header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedAddress)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("updatedAt").exists())
                .andReturn();

        //String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id")
        String street = JsonPath.read(result.getResponse().getContentAsString(), "$.addresses[0].street");

        assertEquals(updatedAddress.getStreet(), street);
    }

    //FIX THIS TEST!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
/*    @DisplayName("It fails to edit a user's address, status code 400 bad request")
    @Test()
    public void updateAddress_fail() throws Exception {
      //  doThrow(new InvalidAddressException("bad")).when(usersService).updateAddress(anyLong(), anyLong(), any(Address.class));
        when(usersService.updateAddress(anyLong(), anyLong(), any(Address.class))).thenReturn(null);

        mockMvc.perform(patch(String.format("/api/users/%d/addresses/%d", 1L, 1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
       //         .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidAddressException));
    }*/


    @DisplayName("It should delete a user's address, status code 202 accepted")
    @Test
    public void deleteAddress() throws Exception {
        mockMvc.perform(delete("/api/users/1/addresses/1").header("Authorization", token))
                .andExpect(status().isAccepted());
        verify(usersService).deleteAddress(1L, 1L);
    }

    @Test
    public void searchUsername_byString_returnsNoContent() throws Exception {
        String username = "bob";

        when(usersService.searchUsers(anyString())).thenReturn(new UsersList(Arrays.asList()));

        mockMvc.perform(get("/api/users?username=" + username).header("Authorization", token))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void createUser_withDuplicateUsernameAndEmail_returnsError() throws Exception {
        User user = new User("bob", "password123", "bob", "smith","bakerBob2@gmail.com");

        when(usersService.createUser(any(User.class))).thenThrow(DuplicateUserException.class);

        mockMvc.perform(post("/api/users").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("It should be able to create an address with a label")
    @Test
    public void createAddress_withLabel() throws Exception {
        Address newAddress = new Address("Test Street", "Test City","Test State", "Test Zipcode", "Test Apartment", "home");
        user.addAddress(newAddress);
        newAddress.setId(1L);

        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        when(usersService.createUser(any(User.class))).thenReturn(user);

        String content = "{\"username\":\"TestUsername3\",\"firstName\":\"First3\",\"lastName\":\"Last3\",\"password\":\"password\",\"email\":\"email3@email.com\",\"addresses\":[{\"street\":\"test street\",\"state\":\"test state\",\"city\":\"test city\",\"zipcode\":\"00000\",\"label\":\"home\"},{\"street\":\"test street2\",\"state\":\"test state2\",\"city\":\"test city2\",\"zipcode\":\"00000\",\"label\":\"work\"}]}";
        mockMvc.perform(post("/api/users").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("addresses[0].label", is("home")))
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("updatedAt").exists());
   }

   @Test
   public void showUser_returnsIDUsernameAvatarEmail() throws Exception {
       UserCondensed user = new UserCondensed(1L, "bakerBob", "myavatar.com", "bakerBob@gmail.com");

       when(usersService.getUserCondensed(anyLong())).thenReturn(user);

       mockMvc.perform(get("/api/users/" + user.getId() + "/condensed").header("Authorization", token))
               .andExpect(status().isOk())
               .andExpect(jsonPath("id").value(user.getId()))
               .andExpect(jsonPath("username").value(user.getUsername()))
               .andExpect(jsonPath("avatar").value(user.getAvatar()))
               .andExpect(jsonPath("email").value(user.getEmail()));
   }

    @Test
    public void showUser_returnsUserCondensedNoContent() throws Exception {
        when(usersService.getUserCondensed(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/users/123243/condensed").header("Authorization", token))
                .andExpect(status().isNoContent());
    }
}
