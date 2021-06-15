package com.galvanize.useraccounts.repository;

import com.galvanize.useraccounts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery=true, value="SELECT * FROM users WHERE LOWER(username) LIKE LOWER(?)")
    List<User> findByUsername(String username);

    @Query(nativeQuery=true, value="SELECT * FROM users WHERE username = ?")
    Optional<User> findByUsernameExactMatch(String username);

    @Query(nativeQuery=true, value="SELECT * FROM users WHERE email = ?")
    Optional<User> findByEmailExactMatch(String email);

    @Query(nativeQuery=true, value="SELECT * FROM users WHERE id = ?")
    Optional<User> findById(Long id);
}
