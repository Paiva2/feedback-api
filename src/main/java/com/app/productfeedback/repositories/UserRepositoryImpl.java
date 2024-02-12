package com.app.productfeedback.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.productfeedback.entities.User;
import com.app.productfeedback.interfaces.UserRepository;

@Repository
public interface UserRepositoryImpl extends UserRepository, JpaRepository<User, UUID> {
}
