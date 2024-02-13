package com.app.productfeedback.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.productfeedback.entities.Comment;
import com.app.productfeedback.interfaces.CommentRepository;

public interface CommentRepositoryImpl extends CommentRepository, JpaRepository<Comment, UUID> {

}
