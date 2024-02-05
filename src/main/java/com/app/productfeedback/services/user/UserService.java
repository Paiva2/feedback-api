package com.app.productfeedback.services.user;

import java.util.Optional;

import com.app.productfeedback.entities.User;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ConflictException;
import com.app.productfeedback.interfaces.user.UserRepositoryInterface;

import org.springframework.security.crypto.bcrypt.BCrypt;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    protected BCrypt bcrypt = new BCrypt();

    private UserRepositoryInterface userRepository;

    public UserService(UserRepositoryInterface userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        if (user == null) {
            throw new BadRequestException("User can't be null.");
        }

        if (user.getPassword().length() < 6) {
            throw new BadRequestException("Password must have at least 6 characters.");
        }

        Optional<User> doesUserAlreadyExists = this.userRepository.findByEmail(user.getEmail());

        if (doesUserAlreadyExists.isPresent()) {
            throw new ConflictException("E-mail already exists.");
        }

        String password_hash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        user.setPassword(password_hash);

        User newUser = this.userRepository.save(user);

        return newUser;
    }
}
