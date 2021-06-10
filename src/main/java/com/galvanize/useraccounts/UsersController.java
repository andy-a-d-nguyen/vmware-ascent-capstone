package com.galvanize.useraccounts;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public User createUser(@Valid @RequestBody User user) throws InvalidUserException {
        return usersService.createUser(user);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody UpdateUserRequest updatedUser) throws InvalidUserException {
        User user = usersService.updateUser(id, updatedUser);

        return user == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(user);
    }

    @PatchMapping("/users/{id}/reset")
    public ResponseEntity<Boolean> update(@PathVariable Long id, @RequestBody UpdateUserPasswordRequest updatedUserPassword) throws InvalidUserException {
        Boolean isUpdated = usersService.updateUserPassword(id, updatedUserPassword.getOldPassword(), updatedUserPassword.getNewPassword());

        return isUpdated == false ? ResponseEntity.noContent().build() : ResponseEntity.ok().build();
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
    
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = usersService.getUser(id);
        return user == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(user);
    }

    /*
     @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void invalidAutoException(InvalidAutoException exception) {

    }



      @RequestMapping("/update")
  @ResponseBody
  public String updateUser(long id, String email, String name) {
    try {
      User user = userDao.findOne(id);
      user.setEmail(email);
      user.setName(name);
      userDao.save(user);
    }
    catch (Exception ex) {
      return "Error updating the user: " + ex.toString();
    }
    return "User successfully updated!";
  }

    */
}
