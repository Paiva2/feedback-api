package com.app.productfeedback.repositories;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import com.app.productfeedback.dto.request.user.UpdateProfileDto;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.interfaces.UserRepository;

@SuppressWarnings("null")
@Repository
public class UserRepositoryTest implements UserRepository {
    private List<User> users = new ArrayList<>();

    @Override
    public Optional<User> findByEmail(String userEmail) {
        Optional<User> user =
                this.users.stream().filter(users -> users.getEmail().equals(userEmail)).findAny();

        return user;
    }

    @Override
    public User save(User user) {
        Optional<User> doesUserExists =
                this.users.stream().filter(users -> users.getId().equals(user.getId())).findAny();

        if (doesUserExists.isEmpty()) {
            user.setId(UUID.randomUUID());

            this.users.add(user);
        } else {
            UpdateProfileDto updatedUser = new UpdateProfileDto();

            BeanUtils.copyProperties(user, updatedUser);
            BeanUtils.copyProperties(updatedUser, doesUserExists);
        }

        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        Optional<User> user =
                this.users.stream().filter(users -> users.getId().equals(userId)).findAny();

        return user;
    }
}
