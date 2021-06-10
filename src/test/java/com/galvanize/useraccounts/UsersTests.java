package com.galvanize.useraccounts;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

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

    @Test
    public void createUser_validatesPasswordIsNotBlank() {
        User user = new User("bakerBob", "baker", "bob", "", "bakerBob@gmail.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

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
}
