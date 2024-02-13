package com.app.productfeedback.dto.response.feedback;

import java.util.List;

import com.app.productfeedback.dto.response.category.CategoryDto;
import com.app.productfeedback.entities.Feedback;

public class ListAllFeedbacksDto {
    private final int currentPage;

    private final int itensPerPage;

    private final long totalElements;

    private final List<FeedbackDto> feedbacks;

    public ListAllFeedbacksDto(int currentPage, int itensPerPage, long totalElements,
            List<Feedback> feedbacks) {
        this.currentPage = currentPage;
        this.itensPerPage = itensPerPage;
        this.totalElements = totalElements;
        this.feedbacks = formatFeedbacklist(feedbacks);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getItensPerPage() {
        return itensPerPage;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public List<FeedbackDto> getFeedbacks() {
        return feedbacks;
    }

    public List<FeedbackDto> formatFeedbacklist(List<Feedback> feedbackList) {
        return feedbackList.stream().map(feedback -> {
            CategoryDto categoryDto = new CategoryDto(feedback.getCategory().getId(),
                    feedback.getCategory().getName());

            return new FeedbackDto(feedback.getId(), feedback.getTitle(), feedback.getDetails(),
                    feedback.getStatus(), categoryDto, feedback.getUpVotes());
        }).toList();
    }
}
