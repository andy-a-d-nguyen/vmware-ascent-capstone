package com.galvanize.useraccounts.request;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ActiveProfiles("test")
public class UserRequestTest {
    @MockBean
    UserRequest userRequest;

    @Test
    void setMethods_work() {
        UserRequest userRequest = new UserRequest("Bob", "baker", "bobbaker@gmail.com", "ajdsfkl12312", true, "myavatar.com");

        userRequest.setFirstName("Sally");
        userRequest.setLastName("Smith");
        userRequest.setEmail("ajskfdl@gmail.com");
        userRequest.setBio("asdfjk12123");
        userRequest.setVerified(false);
        userRequest.setAvatar(null);

        assertEquals("Sally", userRequest.getFirstName());
        assertEquals("Smith", userRequest.getLastName());
        assertEquals("ajskfdl@gmail.com", userRequest.getEmail());
        assertEquals("asdfjk12123", userRequest.getBio());
        assertEquals(false, userRequest.isVerified());
        assertNull(userRequest.getAvatar());
    }
}
