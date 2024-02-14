package com.app.productfeedback.interfaces;

import java.util.Optional;
import java.util.UUID;

import com.app.productfeedback.entities.Answer;

public interface AnswerRepository {
    Answer save(Answer answer);

    Optional<Answer> findById(UUID answerId);

    void deleteById(UUID answerId);
}
