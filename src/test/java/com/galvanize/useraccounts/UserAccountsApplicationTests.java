package com.galvanize.useraccounts;

import com.galvanize.useraccounts.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import com.galvanize.useraccounts.UsersRepository;
import com.galvanize.useraccounts.UsersList;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@TestPropertySource(locations= "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserAccountsApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UsersRepository usersRepository;

    List<User> users;

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

}
