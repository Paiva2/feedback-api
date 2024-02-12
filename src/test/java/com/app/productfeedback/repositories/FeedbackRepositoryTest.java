package com.app.productfeedback.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.enums.FeedbackStatus;
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
            feedback.setStatus(dto.getStatus());

            this.feedbacks.add(feedback);
        } else {
            BeanUtils.copyProperties(dto, doesFeedbackExists.get());

            int existentFeedbackIdx = this.feedbacks.indexOf(doesFeedbackExists.get());

            this.feedbacks.set(existentFeedbackIdx, doesFeedbackExists.get());
        }

        return feedback;
    }

    @Override
    public Page<Feedback> findAllByStatusOrderByCreatedAtDesc(FeedbackStatus status,
            Pageable pageable) {
        int fromIndex = pageable.getPageNumber() * pageable.getPageSize();

        List<Feedback> filterList =
                feedbacks.stream().filter(feedback -> feedback.getStatus().equals(status)).toList();

        if (filterList.size() <= fromIndex) {
            return new PageImpl<Feedback>(Collections.emptyList());
        }

        return new PageImpl<Feedback>(filterList.subList(fromIndex,
                Math.min(fromIndex + pageable.getPageSize(), filterList.size())));
    }

    @Override
    public Page<Feedback> findAllByOrderByCreatedAtDesc(Pageable pageable) {
        int fromIndex = pageable.getPageNumber() * pageable.getPageSize();

        if (this.feedbacks.size() <= fromIndex) {
            return new PageImpl<Feedback>(Collections.emptyList());
        }

        return new PageImpl<Feedback>(this.feedbacks.subList(fromIndex,
                Math.min(fromIndex + pageable.getPageSize(), this.feedbacks.size())));
    }
}
