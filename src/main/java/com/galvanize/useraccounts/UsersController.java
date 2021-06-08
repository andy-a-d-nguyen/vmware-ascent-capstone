package com.galvanize.useraccounts;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UsersController {
    /*
    update, create, show (returns a user), index (return all users)
    */
    UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user){
        return usersService.createUser(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        try {
            usersService.deleteUser(id);
        } catch(UserNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.accepted().build();
    }
}
