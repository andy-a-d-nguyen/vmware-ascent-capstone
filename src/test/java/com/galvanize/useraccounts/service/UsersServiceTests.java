package com.galvanize.useraccounts.service;

import com.galvanize.useraccounts.exception.DuplicateUserException;
import com.galvanize.useraccounts.model.User;
import com.galvanize.useraccounts.repository.UsersRepository;
import com.galvanize.useraccounts.service.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.galvanize.useraccounts.UsersList;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTests {
    @Mock
    UsersRepository usersRepository;

    private UsersService usersService;

    List<User> users;

    @BeforeEach
    void setup() {
        usersService = new UsersService(usersRepository);

        users = new ArrayList<>();

        User user1 = new User("bakerBob", "password123", "baker", "bob","bakerBob1@gmail.com");
        User user2 = new User("bob", "password123", "bob", "smith","bakerBob2@gmail.com");
        User user3 = new User("bobBob", "password123", "bob", "bob","bakerBob3@gmail.com");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        users.add(user1);
        users.add(user2);
        users.add(user3);
    }

    @Test
    void searchUsers_withString_returnsFoundUsers() {
        String username = "bob";

        when(usersRepository.findByUsername(anyString())).thenReturn(users);

        UsersList actual = usersService.searchUsers(username);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(users.size(), actual.size());
    }

    @Test
    void searchUsers_withString_returnsNoContent() {
        String username = "john";

        when(usersRepository.findByUsername(anyString())).thenReturn(new ArrayList<>());

        com.galvanize.useraccounts.UsersList actual = usersService.searchUsers(username);

        assertNull(actual);
    }

    @Test
    void createUser_withDuplicateUsername_throwsError() {
        User user4 = new User("bob", "password123", "bob", "smith","bakerBob2@gmail.com");

        when(usersRepository.save(any(User.class))).thenThrow(DuplicateUserException.class);

        assertThatExceptionOfType(DuplicateUserException.class).isThrownBy( () -> {
            usersService.createUser(user4);
        });
    }

    @Test
    void createUser_withDuplicateEmail_throwsError() {
        User user4 = new User("bob", "password123", "bob", "smith","bakerBob2@gmail.com");

        when(usersRepository.save(any(User.class))).thenThrow(DuplicateUserException.class);

        assertThatExceptionOfType(DuplicateUserException.class).isThrownBy( () -> {
            usersService.createUser(user4);
        });
    }

}
