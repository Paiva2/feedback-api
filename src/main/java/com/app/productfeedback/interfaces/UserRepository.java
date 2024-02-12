package com.app.productfeedback.interfaces;

import java.util.Optional;
import java.util.UUID;

import com.app.productfeedback.entities.User;

public interface UserRepository {

    Optional<User> findByEmail(String userEmail);

    Optional<User> findById(UUID userId);

    User save(User user);
}
