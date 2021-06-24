package com.galvanize.useraccounts.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UsersTests {

    public static ValidatorFactory validatorFactory;
    public static Validator validator;

    @BeforeEach
    public void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterEach
    public void close() {
        validatorFactory.close();
    }

    @Test
    public void createUser_validatesUsernameHasMin5Chars_returnsTrue() {
        User user = new User("bak", "baker", "bob", "password123", "bakerBob@gmail.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createUser_validatesUsernameHasMax20Chars_returnsTrue() {
        User user = new User("bakerBob", "baker", "bob", "password123", "bakerBob@gmail.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void createUser_validatesUsernameHasMax20Chars_returnsFalse() {
        User user = new User("bakerBobUsernameHasMoreThan20Characters", "baker", "bob", "password123", "bakerBob@gmail.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createUser_validatesUsernameIsNotBlank() {
        User user = new User("", "baker", "bob", "password123", "bakerBob@gmail.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createUser_validatesFirstNameIsNotBlank() {
        User user = new User("bakerBob", "password123", "", "password123", "bakerBob@gmail.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createUser_validatesLastNameIsNotBlank() {
        User user = new User("bakerBob", "baker", "", "password123", "bakerBob@gmail.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

//    @Test
//    public void createUser_validatesPasswordIsNotBlank() {
//        User user = new User("bakerBob", "", "bob", "baker", "bakerBob@gmail.com");
//
//        Set<ConstraintViolation<User>> violations = validator.validate(user);
//        assertFalse(violations.isEmpty());
//    }

    @Test
    public void createUser_validatesEmailIsNotBlank() {
        User user = new User("bakerBob", "baker", "bob", "password123", "");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createUser_validatesEmailIsValid() {
        User user = new User("bakerBob", "baker", "bob", "password123", "bakerBob");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createUser_validatesEmailIsLessThan30Characters() {
        User user = new User("bakerBob", "baker", "bob", "password123", "bakerBobHasMoreThan20Characters@gmail.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void createUser_LocalDateTimeFieldExists() {
        User user = new User("bakerBob", "baker", "bob", "password123", "bakerBobHasMoreThan20Characters@gmail.com");
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        assertNotNull(user.getCreatedAt());
    }
    @Test
    void createUser_validatesStreetNotBlank() {
        List<Address> addressList = new ArrayList<>();
        Address address = new Address("", "city", "state", "zipcode", "", "");
        addressList.add(address);

        User user = new User("bakerBob", "password123", "bob", "lastName", "Characters@gmail.com", addressList);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.size(), 1);
    }
    @Test
    void createUser_validatesCityNotBlank() {
        List<Address> addressList = new ArrayList<>();
        Address address = new Address("street", "", "state", "zipcode", "", "");
        addressList.add(address);

        User user = new User("bakerBob", "password123", "bob", "lastName", "Characters@gmail.com", addressList);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.size(), 1);
    }

    @Test
    void createUser_validatesStateNotBlank() {
        List<Address> addressList = new ArrayList<>();
        Address address = new Address("Test", "Test", "", "Test", "", "");
        addressList.add(address);

        User user = new User("bakerBob", "password123", "bob", "lastName", "Characters@gmail.com", addressList);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.size(), 1);
    }

    @Test
    void createUser_validatesZipcodeNotBlank() {
        List<Address> addressList = new ArrayList<>();
        Address address = new Address("Test", "Test", "Test", "", "", "");
        addressList.add(address);

        User user = new User("bakerBob", "password123", "bob", "lastName", "Characters@gmail.com", addressList);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.size(), 1);
    }
}
