package com.bridgelabz.authservice.repository;

import com.bridgelabz.authservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByRole(String role);

    Optional<User> findByPhone(String phone);

    List<User> findByFullNameContainingIgnoreCase(String fullName);
}