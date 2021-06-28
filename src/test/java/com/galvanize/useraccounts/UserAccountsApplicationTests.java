package com.galvanize.useraccounts;

import com.galvanize.useraccounts.UsersList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.useraccounts.model.Address;
import com.galvanize.useraccounts.model.User;
import com.galvanize.useraccounts.model.UserCondensed;
import com.galvanize.useraccounts.repository.AddressRepository;
import com.galvanize.useraccounts.repository.UsersRepository;
import com.galvanize.useraccounts.request.UserPasswordRequest;
import com.galvanize.useraccounts.request.UserRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;

import java.sql.Timestamp;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations= "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserAccountsApplicationTests {
    
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    AddressRepository addressRepository;

    List<User> users;
    List <Address> addresses;
    ObjectMapper mapper = new ObjectMapper();

    @Value("${security.jwt.secret}")
    String JWT_KEY;
    String token;
    
    @BeforeEach
    void setup() {
        users = new ArrayList<>();
        addresses = new ArrayList<>();
        Address address1 = new Address("street1", "city1", "state1", "zipcode1", null, null);
        Address address2 = new Address("street2", "city2", "state2", "zipcode2", null, null);
        Address address3 = new Address("street3", "city3", "state3", "zipcode3", null, null);
        Address address4 = new Address("street4", "city4", "state4", "zipcode4", "apt4", null);

        addresses.add(address1);
        addresses.add(address2);
        addresses.add(address3);

        User user1 = new User("bakerBob", "password123", "baker", "bob","bakerBob1@gmail.com");
        User user2 = new User("bobBobBob", "password123", "bob", "smith","bakerBob2@gmail.com");
        User user3 = new User("bobBob", "password123", "bob", "bob","bakerBob3@gmail.com");
        User user4 = new User("janeDoe", "password123", "jane", "doe","janeDoe@gmail.com");
        User user5 = new User("buddydoggo", "password123", "buddy", "bud","buddydog@gmail.com");
        user5.addAddress(address1);
        user5.addAddress(address2);
        user5.addAddress(address3);
        user5.addAddress(address4);

        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);

        usersRepository.saveAll(users);

        token = getToken("user", Arrays.asList("ROLE_USER"));
    }

    private String getToken(String username, List<String> roles) {
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .claim("name", username)
                .claim("guid", 99)
                .claim("authorities", roles)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 5256000 * 1000L))  // in milliseconds
                .signWith(SignatureAlgorithm.HS512, JWT_KEY.getBytes())
                .compact();

        return String.format("Bearer %s", token);
    }

    @AfterEach
    void teardown() {
        usersRepository.flush();
        usersRepository.deleteAll();
        addressRepository.flush();
        addressRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void createUser_returnsStatusOK() throws JsonProcessingException {
        String uri = "/api/users";

        User user5 = new User("andynguyen", "password123", "Andy", "Nguyen","andynguyen@gmail.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<?> request = new HttpEntity<>(user5, headers);
        ResponseEntity<User> response = restTemplate.postForEntity(uri, request, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user5.getUsername(), response.getBody().getUsername());

        assertEquals(usersRepository.findByUsernameExactMatch(user5.getUsername()).get().getCreatedAt(), response.getBody().getCreatedAt());
        assertEquals(response.getBody().getCreatedAt(), response.getBody().getUpdatedAt());
    }

    @Test
    void createUser_withDupUsername_returnsBadRequest() throws JsonProcessingException {
        User user10 = new User("bakerBob", "password123", "baker", "bob","bakerBob12345@gmail.com");
        String uri = "/api/users";

        String body = mapper.writeValueAsString(user10);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

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
        headers.setBearerAuth(token);

        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<User> response = restTemplate.postForEntity(uri, request, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deleteUser_withID_returnsNoContent() {
        User user = users.get(0);
        Long id = user.getId();
        String uri = "/api/users/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<User> response = restTemplate.exchange(uri, HttpMethod.GET, request, User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        restTemplate.exchange(uri, HttpMethod.DELETE, request, User.class);

        ResponseEntity<User> responseTwo = restTemplate.exchange(uri, HttpMethod.GET, request, User.class);
        assertEquals(HttpStatus.NO_CONTENT, responseTwo.getStatusCode());
    }

    @Test
    void getUser_withID_returnsUser() {
        User user = users.get(0);

        String uri = "/api/users/" + user.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<User> response = restTemplate.exchange(uri, HttpMethod.GET, request, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getUsername(), response.getBody().getUsername());

        assertEquals(usersRepository.findByUsernameExactMatch(user.getUsername()).get().getCreatedAt(), response.getBody().getCreatedAt());
        assertEquals(usersRepository.findByUsernameExactMatch(user.getUsername()).get().getUpdatedAt(), response.getBody().getUpdatedAt());
    }

    @Test
    void getUser_withID_returnsNoContent() {
        String uri = "/api/users/" + 1234L;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<User> response = restTemplate.exchange(uri, HttpMethod.GET, request, User.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
    @Test
    void updateUser_withIDAndBody_returnsUser() {
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        User user = users.get(0);

        String uri = "/api/users/" + user.getId();

        UserRequest request = new UserRequest("Andy", "Nguyen", user.getPassword(), "andynguyen@gmail.com", user.getBio(), user.isVerified(), user.getAvatar());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(token);

        HttpEntity<UserRequest> patchRequest = new HttpEntity<>(request, headers);

        ResponseEntity<User> response = restTemplate.exchange(uri, HttpMethod.PATCH, patchRequest, User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getFirstName()).isEqualTo(request.getFirstName());
        assertThat(response.getBody().getLastName()).isEqualTo(request.getLastName());
        assertThat(response.getBody().getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getBody().getBio()).isEqualTo(request.getBio());
        assertThat(response.getBody().isVerified()).isEqualTo(request.isVerified());
        assertThat(response.getBody().getAvatar()).isEqualTo(request.getAvatar());

        assertEquals(usersRepository.findByUsernameExactMatch(user.getUsername()).get().getCreatedAt(), response.getBody().getCreatedAt());
        assertEquals(usersRepository.findByUsernameExactMatch(user.getUsername()).get().getUpdatedAt(), response.getBody().getUpdatedAt());
        assertTrue(response.getBody().getCreatedAt().before(response.getBody().getUpdatedAt()));
    }
    @Test
    void updateUser_withIDAndBody_returnsNoContent() {
        User user = users.get(0);

        String uri = "/api/users/" + 1234L;

        UserRequest request = new UserRequest("Andy", "Nguyen", user.getPassword(), "andynguyen@gmail.com", user.getBio(), user.isVerified(), user.getAvatar());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(token);

        HttpEntity<UserRequest> patchRequest = new HttpEntity<>(request, headers);

        ResponseEntity<User> response = restTemplate.exchange(uri, HttpMethod.PATCH, patchRequest, User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertNull(response.getBody());
    }

    @Test
    void updatePassword_withIDAndRequestBody_returnsSuccessStatus() {
        User user = users.get(0);

        String uri = "/api/users/" + user.getId() + "/reset";

        UserPasswordRequest passwordRequest = new UserPasswordRequest("password123", "newPassword");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(token);

        HttpEntity<?> patchRequest = new HttpEntity<>(passwordRequest, headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(uri, HttpMethod.PATCH, patchRequest, Boolean.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Timestamp created = usersRepository.findByUsernameExactMatch(user.getUsername()).get().getCreatedAt();
        Timestamp updated = usersRepository.findByUsernameExactMatch(user.getUsername()).get().getUpdatedAt();

        assertNotEquals(usersRepository.findByUsernameExactMatch(user.getUsername()).get().getCreatedAt(), usersRepository.findByUsernameExactMatch(user.getUsername()).get().getUpdatedAt());
    }

    @Test
    void updatePassword_withIDAndBadPassword_returnsNoContent() {
        User user = users.get(0);

        String uri = "/api/users/" + user.getId() + "/reset";

        UserPasswordRequest passwordRequest = new UserPasswordRequest("password", "newpassword");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(token);

        HttpEntity<?> patchRequest = new HttpEntity<>(passwordRequest, headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(uri, HttpMethod.PATCH, patchRequest, Boolean.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void updatePassword_withInvalidID_returnsNoContent() {
        String uri = "/api/users/" + 1234L + "/reset";

        UserPasswordRequest passwordRequest = new UserPasswordRequest("password123", "newpassword");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(token);

        HttpEntity<?> patchRequest = new HttpEntity<>(passwordRequest, headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(uri, HttpMethod.PATCH, patchRequest, Boolean.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void CreateUser_withValidAttr_nullAddress_returnsUser() throws JsonProcessingException {
        String uri = "/api/users";
        String body = "{\"username\":\"TestUsername3\",\"firstName\":\"First3\",\"lastName\":\"Last3\",\"password\":\"password\",\"email\":\"email3@email.com\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(token);

        HttpEntity<?> postRequest = new HttpEntity<>(body, headers);

        ResponseEntity<User> response = restTemplate.postForEntity(uri, postRequest, User.class);

        assertEquals(HttpStatus.OK,response.getStatusCode());

        assertEquals(response.getBody().getCreatedAt(), response.getBody().getUpdatedAt());

    }

    @Test
    void CreateUser_withValidAttr_withAddress_returnsUser() throws JsonProcessingException {
        String uri = "/api/users";
        String body = "{\"username\":\"TestUsername3\",\"firstName\":\"First3\",\"lastName\":\"Last3\",\"password\":\"password\",\"email\":\"email3@email.com\",\"addresses\":[{\"street\":\"test street\",\"state\":\"test state\",\"city\":\"test city\",\"zipcode\":\"00000\"},{\"street\":\"test street2\",\"state\":\"test state2\",\"city\":\"test city2\",\"zipcode\":\"00000\"}]}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(token);

        HttpEntity<?> postRequest = new HttpEntity<>(body, headers);

        ResponseEntity<User> response = restTemplate.postForEntity(uri, postRequest, User.class);

        assertEquals(HttpStatus.OK,response.getStatusCode());

        assertEquals(response.getBody().getCreatedAt(), response.getBody().getUpdatedAt());
    }

    @Test
    void editUser_allowedAttributes_success() throws JsonProcessingException, InterruptedException {
        String searchParams = "buddydoggo";
        String getUri = "/api/users?username=" + searchParams;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(token);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<UsersList> getResponse = restTemplate.exchange(getUri, HttpMethod.GET, request, UsersList.class);
        User getUser = Objects.requireNonNull(getResponse.getBody().getUsers().get(0));
        Long getUserId = getUser.getId();

        getUser.setFirstName("updatedFirst");
        getUser.setLastName("updatedLast");
        getUser.setEmail("updated@email.com");
        HttpEntity<?> patchRequest = new HttpEntity<>(getUser, headers);
        ResponseEntity <User> patchResponse = restTemplate.exchange("/api/users/" + getUserId, HttpMethod.PATCH, patchRequest, User.class);

        assertEquals(Objects.requireNonNull(patchResponse.getBody()).getFirstName(), "updatedFirst");
        assertEquals(Objects.requireNonNull(patchResponse.getBody()).getLastName(), "updatedLast");
        assertEquals(Objects.requireNonNull(patchResponse.getBody()).getEmail(), "updated@email.com");
        assertEquals(HttpStatus.OK,patchResponse.getStatusCode());

        assertEquals(usersRepository.findByUsernameExactMatch(getUser.getUsername()).get().getCreatedAt(), patchResponse.getBody().getCreatedAt());
        assertEquals(usersRepository.findByUsernameExactMatch(getUser.getUsername()).get().getUpdatedAt(), patchResponse.getBody().getUpdatedAt());
        assertTrue(patchResponse.getBody().getCreatedAt().before(patchResponse.getBody().getUpdatedAt()));
    }

    @Test
    void editUserAddress_success() throws JsonProcessingException {
        String searchParams = "buddydoggo";
        String getUri = "/api/users?username=" + searchParams;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(token);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<UsersList> getResponse = restTemplate.exchange(getUri, HttpMethod.GET, request, UsersList.class);

        User getUser = Objects.requireNonNull(getResponse.getBody().getUsers().get(0));
        Long getUserId = getUser.getId();
        Address getAddress = getUser.getAddresses().get(0);
        Long getAddressId = getAddress.getId();
        getAddress.setStreet("updatedStreet");
        getAddress.setCity("updatedCity");
        getAddress.setState("updatedState");

        String uri = String.format("/api/users/%d/addresses/%d", getUserId, getAddressId);
        HttpEntity<?> patchRequest = new HttpEntity<>(getAddress, headers);

        ResponseEntity <User> response = restTemplate.exchange(uri, HttpMethod.PATCH, patchRequest, User.class);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(Objects.requireNonNull(response.getBody()).getAddresses().get(0).getStreet(), "updatedStreet");
        assertEquals(Objects.requireNonNull(response.getBody()).getAddresses().get(0).getCity(), "updatedCity");
        assertEquals(Objects.requireNonNull(response.getBody()).getAddresses().get(0).getState(), "updatedState");

        assertEquals(usersRepository.findByUsernameExactMatch(getUser.getUsername()).get().getCreatedAt(), response.getBody().getCreatedAt());
        assertEquals(usersRepository.findByUsernameExactMatch(getUser.getUsername()).get().getUpdatedAt(), response.getBody().getUpdatedAt());
        assertTrue(response.getBody().getCreatedAt().before(response.getBody().getUpdatedAt()));
    }

    @Test
    void editUserAddress_fails_userNotFound() throws JsonProcessingException {
        String uri = "/api/users/100/addresses/1";
        String body = "{\"id\":1,\"street\":\"WOOOOOO\",\"city\":\"LMAO\",\"state\":\"test state\",\"zipcode\":\"00000\",\"apartment\":null}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(token);

        HttpEntity<?> patchRequest = new HttpEntity<>(body, headers);

        ResponseEntity <User> response = restTemplate.exchange(uri, HttpMethod.PATCH, patchRequest, User.class);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
    }

    @Test
    void editUserAddress_fails_AddressNotFound() throws JsonProcessingException {
        String uri = "/api/users/1/addresses/100";
        String body = "{\"id\":1,\"street\":\"WOOOOOO\",\"city\":\"LMAO\",\"state\":\"test state\",\"zipcode\":\"00000\",\"apartment\":null}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(token);

        HttpEntity<?> patchRequest = new HttpEntity<>(body, headers);

        ResponseEntity<User> response = restTemplate.exchange(uri, HttpMethod.PATCH, patchRequest, User.class);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
    }


    @Test
    void deleteUserAddress_success() throws JsonProcessingException {
        String searchParams = "buddydoggo";
        String getUri = "/api/users?username=" + searchParams;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(token);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<UsersList> getResponse = restTemplate.exchange(getUri, HttpMethod.GET, request, UsersList.class);

        User getUser = Objects.requireNonNull(getResponse.getBody().getUsers().get(0));
        Long getUserId = getUser.getId();
        Long getAddressId = getUser.getAddresses().get(0).getId();

        String deleteUri = String.format("/api/users/%d/addresses/%d", getUserId, getAddressId);

        restTemplate.exchange(deleteUri, HttpMethod.DELETE, request, User.class);

        ResponseEntity<UsersList> checkUsers = restTemplate.exchange(getUri, HttpMethod.GET, request, UsersList.class);

        getUri = String.format("/api/users/%d", getUserId);

        ResponseEntity<User> response = restTemplate.exchange(getUri, HttpMethod.GET, request, User.class);

        int actual = Objects.requireNonNull(response.getBody()).getAddresses().size();

        assertEquals(3, actual);

        assertEquals(usersRepository.findByUsernameExactMatch(getUser.getUsername()).get().getCreatedAt(), response.getBody().getCreatedAt());
        assertEquals(usersRepository.findByUsernameExactMatch(getUser.getUsername()).get().getUpdatedAt(), response.getBody().getUpdatedAt());
        assertTrue(usersRepository.findByUsernameExactMatch(getUser.getUsername()).get().getCreatedAt().before(usersRepository.findByUsernameExactMatch(getUser.getUsername()).get().getUpdatedAt()));
    }

    @Test
    void deleteUserAddress_failure_addressNotFound() throws JsonProcessingException {
        String searchParams = "buddydoggo";
        String getUri = "/api/users?username=" + searchParams;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(token);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<UsersList> getResponse = restTemplate.exchange(getUri, HttpMethod.GET, request, UsersList.class);

        User getUser = Objects.requireNonNull(getResponse.getBody().getUsers().get(0));
        Long getUserId = getUser.getId();

        String uri = String.format("/api/users/%d/addresses/234234", getUserId);

        getUri = String.format("/api/users/%d/addresses", getUserId);

        ResponseEntity<User> response = restTemplate.exchange(getUri, HttpMethod.GET, request, User.class);

        int expected =  Objects.requireNonNull(response.getBody()).getAddresses().size();

        restTemplate.exchange(uri, HttpMethod.DELETE, request, User.class);

        response = restTemplate.exchange(getUri, HttpMethod.GET, request, User.class);

        int actual = Objects.requireNonNull(response.getBody()).getAddresses().size();

        assertEquals(expected, actual);
    }


    @Test
    void getUser_withID_returnsUserCondensed() {
        User user = users.get(0);

        String uri = "/api/users/" + user.getId() + "/condensed";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<UserCondensed> response = restTemplate.exchange(uri, HttpMethod.GET, request, UserCondensed.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getId(), response.getBody().getId());
        assertEquals(user.getUsername(), response.getBody().getUsername());
        assertEquals(user.getAvatar(), response.getBody().getAvatar());
        assertEquals(user.getEmail(), response.getBody().getEmail());
    }

}
