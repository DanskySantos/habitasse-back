package com.project.habitasse.security.user.repository;

import com.project.habitasse.security.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndExcludedFalse(String email);

    Optional<User> findByUsernameAndExcludedFalse(String username);
}
