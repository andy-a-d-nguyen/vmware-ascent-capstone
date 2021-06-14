package com.galvanize.useraccounts;

import com.galvanize.useraccounts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery=true, value="SELECT * FROM users WHERE LOWER(username) LIKE ?")
    List<User> findByUsername(String username);
}
