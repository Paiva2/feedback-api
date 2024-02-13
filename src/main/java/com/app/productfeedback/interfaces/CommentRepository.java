package com.app.productfeedback.interfaces;

import com.app.productfeedback.entities.Comment;

public interface CommentRepository {
    Comment save(Comment feedback);
}
