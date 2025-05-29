package com.school.repository;

import com.school.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> { // access users
    Optional<User> findByUsername(String username);
}
