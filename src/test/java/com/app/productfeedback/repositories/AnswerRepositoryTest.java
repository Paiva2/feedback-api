package com.app.productfeedback.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

import com.app.productfeedback.entities.Answer;
import com.app.productfeedback.interfaces.AnswerRepository;

public class AnswerRepositoryTest implements AnswerRepository {
    protected List<Answer> answers = new ArrayList<>();

    @Override
    public Answer save(Answer answer) {
        Answer handleAnswer;

        Optional<Answer> doesAnswerExists = this.answers.stream()
                .filter(answers -> answers.getId().equals(answer.getId())).findAny();

        if (doesAnswerExists.isEmpty()) {
            answer.setId(UUID.randomUUID());
            this.answers.add(answer);

            handleAnswer = answer;
        } else {
            int currentAnswerIdx = this.answers.indexOf(doesAnswerExists.get());
            this.answers.set(currentAnswerIdx, answer);

            handleAnswer = this.answers.get(currentAnswerIdx);
        }

        return handleAnswer;
    }

    @Override
    public Optional<Answer> findById(UUID answerId) {
        return this.answers.stream().filter(answer -> answer.getId().equals(answerId)).findAny();
    }

    @Override
    public void deleteById(UUID answerId) {
        this.answers.removeIf(answer -> answer.getId().equals(answerId));
    }
}
