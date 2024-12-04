package com.james.spring_security_configuration.repositories;

//import com.example.springsecurity.models.User;
import com.james.spring_security_configuration.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findFirstByEmail(String email);


}
