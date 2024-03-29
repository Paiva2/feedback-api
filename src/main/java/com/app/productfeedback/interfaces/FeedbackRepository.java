package com.app.productfeedback.interfaces;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.enums.FeedbackStatus;

public interface FeedbackRepository {
    Feedback save(Feedback newFeedback);

    Page<Feedback> findAllByStatusOrderByCreatedAtDesc(FeedbackStatus status, Pageable pageable);

    Page<Feedback> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Optional<Feedback> findById(UUID feedbackId);

    void deleteById(UUID feedbackId);
}
