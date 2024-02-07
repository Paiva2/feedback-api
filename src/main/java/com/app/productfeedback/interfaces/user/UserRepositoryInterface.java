package com.app.productfeedback.interfaces.user;

import java.util.Optional;
import java.util.UUID;
import com.app.productfeedback.entities.User;

public interface UserRepositoryInterface {

    Optional<User> findByEmail(String userEmail);

    Optional<User> findById(UUID userId);

    User save(User user);
}
