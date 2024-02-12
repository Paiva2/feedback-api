package com.app.productfeedback.interfaces;

import com.app.productfeedback.entities.Feedback;

public interface FeedbackRepository {
    Feedback save(Feedback newFeedback);
}
