package com.app.productfeedback.interfaces;

import java.util.Optional;
import java.util.UUID;

import com.app.productfeedback.entities.Comment;

public interface CommentRepository {
    Comment save(Comment comment);

    Optional<Comment> findById(UUID commentId);

    void deleteById(UUID commentId);
}
