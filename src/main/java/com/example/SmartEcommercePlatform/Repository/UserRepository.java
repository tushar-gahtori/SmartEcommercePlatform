package com.example.SmartEcommercePlatform.Repository;

import com.example.SmartEcommercePlatform.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
