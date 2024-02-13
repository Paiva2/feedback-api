package com.app.productfeedback.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.productfeedback.entities.Answer;
import com.app.productfeedback.interfaces.AnswerRepository;

public interface AnswerRepositoryImpl extends AnswerRepository, JpaRepository<Answer, UUID> {

}
