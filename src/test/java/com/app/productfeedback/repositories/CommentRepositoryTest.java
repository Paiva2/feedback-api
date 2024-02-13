package com.app.productfeedback.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

import com.app.productfeedback.entities.Comment;
import com.app.productfeedback.interfaces.CommentRepository;

public class CommentRepositoryTest implements CommentRepository {
    protected List<Comment> comments = new ArrayList<>();

    @Override
    public Comment save(Comment comment) {
        Comment handleComment;

        Optional<Comment> doesCommentExists = this.comments.stream()
                .filter(comments -> comments.getId().equals(comment.getId())).findFirst();

        if (doesCommentExists.isEmpty()) {
            comment.setId(UUID.randomUUID());

            this.comments.add(comment);

            handleComment = comment;
        } else {
            int findCommentIdx = this.comments.indexOf(doesCommentExists.get());

            this.comments.set(findCommentIdx, comment);

            handleComment = this.comments.get(findCommentIdx);
        }

        return handleComment;
    }

}
