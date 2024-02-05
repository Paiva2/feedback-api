package com.app.productfeedback.interfaces.user;

import java.util.Optional;

import com.app.productfeedback.entities.User;

public interface UserRepositoryInterface {

    Optional<User> findByEmail(String userEmail);

    User save(User user);
}
