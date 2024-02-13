package com.app.productfeedback.entities;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import com.app.productfeedback.enums.FeedbackStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.*;

@Entity(name = "tb_feedbacks")
public class Feedback {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String details;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name = "fk_user_id")
    private UUID fkUserId;

    @Column(name = "fk_category_id")
    private UUID fkCategoryId;

    @Column(name = "up_votes")
    private int upVotes;

    @Enumerated(EnumType.STRING)
    private FeedbackStatus status = FeedbackStatus.SUGGESTION;

    @OneToOne
    @JoinColumn(name = "fk_user_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @OneToOne
    @JoinColumn(name = "fk_category_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category;

    public Feedback() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
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

    public UUID getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(UUID fkUserId) {
        this.fkUserId = fkUserId;
    }

    public UUID getFkCategoryId() {
        return fkCategoryId;
    }

    public void setFkCategory(UUID fkCategoryId) {
        this.fkCategoryId = fkCategoryId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public FeedbackStatus getStatus() {
        return status;
    }

    public void setStatus(FeedbackStatus status) {
        this.status = status;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }
}
