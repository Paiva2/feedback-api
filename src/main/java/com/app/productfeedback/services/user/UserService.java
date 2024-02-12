package com.app.productfeedback.services.user;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import com.app.productfeedback.dto.request.user.UpdateProfileDto;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.UserRole;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ConflictException;
import com.app.productfeedback.exceptions.ForbiddenException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.interfaces.UserRepositoryInterface;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import java.beans.PropertyDescriptor;

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

        if (user.getSecretAnswer() == null || user.getSecretQuestion() == null) {
            throw new BadRequestException("Secret question and answer must be provided.");
        }

        Optional<User> doesUserAlreadyExists = this.userRepository.findByEmail(user.getEmail());

        if (doesUserAlreadyExists.isPresent()) {
            throw new ConflictException("E-mail already exists.");
        }

        String password_hash = bcrypt.encode(user.getPassword());


        user.setPassword(password_hash);
        user.setRole(UserRole.USER);

        User userCreated = this.userRepository.save(user);

        return userCreated;
    }

    public User forgotPassword(User user) {
        if (user == null) {
            throw new BadRequestException("User can't be null.");
        }

        Optional<User> doesUserExists = this.userRepository.findByEmail(user.getEmail());

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        if (user.getSecretAnswer() == null || user.getSecretQuestion() == null) {
            throw new BadRequestException("Secret answer and question must be provided.");
        }

        User getUser = doesUserExists.get();

        boolean userSecretQuestionMatch =
                getUser.getSecretQuestion().equals(user.getSecretQuestion());

        boolean userSecretAnswer = getUser.getSecretAnswer().equals(user.getSecretAnswer());

        if (!userSecretQuestionMatch) {
            throw new ForbiddenException("Secret question doesn't match.");
        }

        if (!userSecretAnswer) {
            throw new ForbiddenException("Secret answer doesn't match.");
        }

        if (user.getPassword().length() < 6) {
            throw new BadRequestException("Password must have at least 6 characters.");
        }

        String hashNewPassword = this.bcrypt.encode(user.getPassword());

        getUser.setPassword(hashNewPassword);

        User userUpdated = this.userRepository.save(getUser);

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

        boolean doesPasswordMatches =
                this.bcrypt.matches(user.getPassword(), getUser.getPassword());

        if (!doesPasswordMatches) {
            throw new ForbiddenException("Wrong credentials.");
        }

        return getUser;
    }

    public User updateProfile(UpdateProfileDto userUpdated, UUID userId) {
        if (userUpdated == null) {
            throw new BadRequestException("User can't be null.");
        }

        if (userId == null) {
            throw new BadRequestException("User id can't be null.");
        }

        Optional<User> doesUserExists = this.userRepository.findById(userId);

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

        BeanWrapper updatedCopy = new BeanWrapperImpl(userUpdated);
        BeanWrapper sourceCopy = new BeanWrapperImpl(getUser);

        List<PropertyDescriptor> fieldsToUpdate = List.of(updatedCopy.getPropertyDescriptors());

        fieldsToUpdate.forEach(field -> {
            String fieldName = field.getName();
            Object fieldValue = updatedCopy.getPropertyValue(fieldName);

            if (fieldValue != null & !fieldName.equals("class")) {
                sourceCopy.setPropertyValue(fieldName, fieldValue);
            }
        });

        User performUpdate = this.userRepository.save(getUser);

        return performUpdate;
    }

    public Map<String, Object> profile(UUID userId) {
        if (userId == null) {
            throw new BadRequestException("User can't be null.");
        }

        Optional<User> doesUserExists = this.userRepository.findById(userId);

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        Map<String, Object> formattedUser = new LinkedHashMap<>();

        User getUser = doesUserExists.get();

        formattedUser.put("id", getUser.getId());
        formattedUser.put("email", getUser.getEmail());
        formattedUser.put("username", getUser.getUsername());
        formattedUser.put("secretQuestion", getUser.getSecretQuestion());
        formattedUser.put("createdAt", getUser.getCreatedAt());

        return formattedUser;
    }
}
