package com.galvanize.useraccounts.request;

import com.galvanize.useraccounts.request.UpdateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateUserRequestTest {
    @MockBean
    UpdateUserRequest updateUserRequest;

    @Test
    void setMethods_work (){
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("Bob", "baker", "password123", "bobbaker@gmail.com", "11123 Bob Street", "ajdsfkl12312", true);

        updateUserRequest.setFirstName("Sally");
        updateUserRequest.setLastName("Smith");
        updateUserRequest.setPassword("121password");
        updateUserRequest.setEmail("ajskfdl@gmail.com");
        updateUserRequest.setCreditCard("asdfjk12123");
        updateUserRequest.setVerify(false);

        assertEquals("Sally", updateUserRequest.getFirstName());
        assertEquals("Smith", updateUserRequest.getLastName());
        assertEquals("121password", updateUserRequest.getPassword());
        assertEquals("ajskfdl@gmail.com", updateUserRequest.getEmail());
        assertEquals("asdfjk12123", updateUserRequest.getCreditCard());
        assertEquals(false, updateUserRequest.isVerify());


    }
}
