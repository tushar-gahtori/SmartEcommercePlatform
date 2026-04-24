package com.example.SmartEcommercePlatform.Repository;

import com.example.SmartEcommercePlatform.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);
}
