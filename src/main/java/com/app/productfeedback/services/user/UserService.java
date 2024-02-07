package com.app.productfeedback.services.user;

import java.util.Optional;

import com.app.productfeedback.entities.User;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ConflictException;
import com.app.productfeedback.exceptions.ForbiddenException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.interfaces.user.UserRepositoryInterface;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    protected BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(6);

    private final UserRepositoryInterface userRepository;

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

        String password_hash = bcrypt.encode(user.getPassword());

        user.setPassword(password_hash);

        User newUser = this.userRepository.save(user);

        return newUser;
    }

    // TODO: BETTER VALIDATIONS TO UPDATE AN USER PASSWORD EX: SECURITY QUESTIONS OR NEW PASSWORD
    // VIA E-MAIL
    public User forgotPassword(User user) {
        if (user == null) {
            throw new BadRequestException("User can't be null.");
        }

        Optional<User> doesUserExists = this.userRepository.findByEmail(user.getEmail());

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        if (user.getPassword().length() < 6) {
            throw new BadRequestException("Password must have at least 6 characters.");
        }

        String hashNewPassword = bcrypt.encode(user.getPassword());

        doesUserExists.get().setPassword(hashNewPassword);

        User userUpdated = this.userRepository.save(doesUserExists.get());

        return userUpdated;
    }

    public User auth(User user) {
        if (user == null) {
            throw new BadRequestException("User can't be null.");
        }

        Optional<User> doesUserExists = this.userRepository.findByEmail(user.getEmail());

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        User getUser = doesUserExists.get();

        boolean doesPasswordMatches = bcrypt.matches(user.getPassword(), getUser.getPassword());

        if (!doesPasswordMatches) {
            throw new ForbiddenException("Wrong credentials.");
        }

        return getUser;
    }

    public User updateProfile(User userUpdated) {
        if (userUpdated == null) {
            throw new BadRequestException("User can't be null.");
        }

        if (userUpdated.getId() == null) {
            throw new BadRequestException("User id can't be null.");
        }

        Optional<User> doesUserExists = this.userRepository.findById(userUpdated.getId());

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        if (userUpdated.getEmail() != null) {
            Optional<User> emailAlreadyExists =
                    this.userRepository.findByEmail(userUpdated.getEmail());

            if (emailAlreadyExists.isPresent()) {
                throw new ConflictException("E-mail already exists.");
            }
        }

        User getUser = doesUserExists.get();

        if (userUpdated.getPassword() != null) {
            if (userUpdated.getPassword().length() < 6) {
                throw new BadRequestException("Password must have at least 6 characters.");
            }

            String hashedNewPassword = this.bcrypt.encode(userUpdated.getPassword());

            userUpdated.setPassword(hashedNewPassword);
        }

        BeanUtils.copyProperties(userUpdated, getUser);

        User performUpdate = this.userRepository.save(getUser);

        return performUpdate;
    }
}
