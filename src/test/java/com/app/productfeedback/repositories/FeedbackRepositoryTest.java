package com.app.productfeedback.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;

import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.interfaces.FeedbackRepository;

@SuppressWarnings("null")
public class FeedbackRepositoryTest implements FeedbackRepository {
    protected List<Feedback> feedbacks = new ArrayList<>();

    @Override
    public Feedback save(Feedback dto) {
        Feedback feedback = new Feedback();

        Optional<Feedback> doesFeedbackExists = this.feedbacks.stream()
                .filter(feedbacks -> feedbacks.getId().equals(dto.getId())).findFirst();

        if (doesFeedbackExists.isEmpty()) {
            feedback.setId(UUID.randomUUID());
            feedback.setTitle(dto.getTitle());
            feedback.setDetails(dto.getDetails());
            feedback.setFkUserId(dto.getFkUserId());
            feedback.setFkCategoryId(dto.getFkCategoryId());

            this.feedbacks.add(feedback);
        } else {
            BeanUtils.copyProperties(dto, doesFeedbackExists.get());

            int existentFeedbackIdx = this.feedbacks.indexOf(doesFeedbackExists.get());

            this.feedbacks.set(existentFeedbackIdx, doesFeedbackExists.get());
        }

        return feedback;
    }

}
