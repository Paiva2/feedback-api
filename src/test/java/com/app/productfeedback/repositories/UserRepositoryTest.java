package com.app.productfeedback.repositories;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import com.app.productfeedback.entities.User;
import com.app.productfeedback.interfaces.user.UserRepositoryInterface;

@Repository
public class UserRepositoryTest implements UserRepositoryInterface {
    private List<User> users = new ArrayList<>();

    @Override
    public Optional<User> findByEmail(String userEmail) {
        Optional<User> user =
                this.users.stream().filter(users -> users.getEmail().equals(userEmail)).findAny();

        return user;
    }

    @Override
    public User save(User user) {
        this.users.add(user);

        return user;
    }
}
