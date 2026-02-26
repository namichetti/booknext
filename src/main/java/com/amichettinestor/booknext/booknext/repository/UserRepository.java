package com.amichettinestor.booknext.booknext.repository;

import com.amichettinestor.booknext.booknext.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String newUsername);
}
