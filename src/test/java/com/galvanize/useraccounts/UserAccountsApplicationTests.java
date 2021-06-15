package com.galvanize.useraccounts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.useraccounts.model.User;
import com.galvanize.useraccounts.repository.UsersRepository;
import com.galvanize.useraccounts.request.UserAvatarRequest;
import com.galvanize.useraccounts.request.UserRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;

import com.galvanize.useraccounts.UsersList;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@TestPropertySource(locations= "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserAccountsApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UsersRepository usersRepository;

    List<User> users;
    ObjectMapper mapper = new ObjectMapper();
    
    @BeforeEach
    void setup() {
        users = new ArrayList<>();

        User user1 = new User("bakerBob", "password123", "baker", "bob","bakerBob1@gmail.com");
        User user2 = new User("bobBobBob", "password123", "bob", "smith","bakerBob2@gmail.com");
        User user3 = new User("bobBob", "password123", "bob", "bob","bakerBob3@gmail.com");
        User user4 = new User("janeDoe", "password123", "jane", "doe","janeDoe@gmail.com");

        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);

        usersRepository.saveAll(users);
    }

    @AfterEach
    void teardown() {
        usersRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void searchUser_withString_returnsFoundUsers() {
        String searchParams = "bob";
        String uri = "/api/users?username=" + searchParams;

        ResponseEntity<UsersList> response = restTemplate.getForEntity(uri, UsersList.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals(3, response.getBody().size());
    }

    @Test
    void createUser_withDupUsername_returnsBadRequest() throws JsonProcessingException {
        User user5 = new User("bakerBob", "password123", "baker", "bob","bakerBob12345@gmail.com");
        String uri = "/api/users";

        String body = mapper.writeValueAsString(user5);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<User> response = restTemplate.postForEntity(uri, request, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUser_withDupEmail_returnsBadRequest() throws JsonProcessingException {
        User user5 = new User("bakerBob", "password123", "baker", "bob","bakerBob1@gmail.com");
        String uri = "/api/users";

        String body = mapper.writeValueAsString(user5);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<User> response = restTemplate.postForEntity(uri, request, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deleteUser_withID_returnsNoContent() {
        User user = users.get(0);
        Long id = user.getId();
        String uri = "/api/users/" + id;

        ResponseEntity<User> response = restTemplate.getForEntity(uri, User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        restTemplate.delete(uri);

        ResponseEntity<User> responseTwo = restTemplate.getForEntity(uri, User.class);
        assertEquals(HttpStatus.NO_CONTENT, responseTwo.getStatusCode());
    }

    @Test
    void setAvatar_withIDAndURL_returnsUser() throws JsonProcessingException {
        User user = users.get(0);
        Long id = user.getId();
        String uri = "/api/users/" + id;
        String avatar = "https://myavatar.com";

        UserAvatarRequest request = new UserAvatarRequest(avatar);

        String body = mapper.writeValueAsString(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> requestTwo = new HttpEntity<>(body, headers);
        ResponseEntity<User> response = restTemplate.postForEntity(uri, requestTwo, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(avatar, response.getBody().getAvatar());
    }

    @Test
    void setAvatar_withIDAndURL_returnsNoContent() throws JsonProcessingException {
        String uri = "/api/users/" + 12345L;
        String avatar = "https://myavatar.com";

        UserAvatarRequest request = new UserAvatarRequest(avatar);

        String body = mapper.writeValueAsString(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> requestTwo = new HttpEntity<>(body, headers);
        ResponseEntity<User> response = restTemplate.postForEntity(uri, requestTwo, User.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getUser_withID_returnsUser() {
        User user = users.get(0);

        String uri = "/api/users/" + user.getId();

        ResponseEntity<User> response = restTemplate.getForEntity(uri, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getUsername(), response.getBody().getUsername());
    }

    @Test
    void getUser_withID_returnsNoContent() {
        String uri = "/api/users/" + 1234L;

        ResponseEntity<User> response = restTemplate.getForEntity(uri, User.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void updateUser_withIDAndBody_returnsUser() {
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        User user = users.get(0);

        String uri = "/api/users/" + user.getId();

        UserRequest request = new UserRequest("Andy", "Nguyen", user.getPassword(), "andynguyen@gmail.com", user.getCreditCard(), user.isVerified());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<UserRequest> patchRequest = new HttpEntity<>(request, headers);

        ResponseEntity<User> response = restTemplate.exchange(uri, HttpMethod.PATCH, patchRequest, User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getFirstName()).isEqualTo(request.getFirstName());
        assertThat(response.getBody().getLastName()).isEqualTo(request.getLastName());
        assertThat(response.getBody().getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getBody().getCreditCard()).isEqualTo(request.getCreditCard());
        assertThat(response.getBody().isVerified()).isEqualTo(request.isVerify());
    }
}
