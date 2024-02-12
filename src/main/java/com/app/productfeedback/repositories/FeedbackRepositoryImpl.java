package com.app.productfeedback.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.interfaces.FeedbackRepository;

public interface FeedbackRepositoryImpl extends FeedbackRepository, JpaRepository<Feedback, UUID> {

}
