package com.app.productfeedback.entities;

import java.util.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

@Entity(name = "tb_comments")
public class Comment {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @Column(nullable = false)
    private String comment;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name = "fk_user_id", nullable = false)
    private String userId;

    @Column(name = "fk_feedback_id", nullable = false)
    private String feedbackId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_feedback_id", insertable = false, updatable = false)
    private Feedback feedback;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_user_id", insertable = false, updatable = false)
    private Feedback user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comment")
    private List<Answer> answer;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public Feedback getUser() {
        return user;
    }

    public void setUser(Feedback user) {
        this.user = user;
    }

    public List<Answer> getAnswer() {
        return answer;
    }

    public void setAnswer(List<Answer> answer) {
        this.answer = answer;
    }
}
