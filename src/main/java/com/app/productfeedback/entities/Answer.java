package com.app.productfeedback.entities;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

@Entity(name = "tb_answer")
public class Answer {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @Column(nullable = false)
    private String answer;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name = "fk_user_id", nullable = false)
    private UUID userId;

    @Column(name = "fk_answering_to", nullable = false)
    private UUID answeringToId;

    @Column(name = "fk_comment_id", nullable = false)
    private UUID commentId;

    @ManyToOne
    @JoinColumn(name = "fk_user_id", updatable = false, insertable = false)
    public User user;

    @ManyToOne
    @JoinColumn(name = "fk_answering_to", updatable = false, insertable = false)
    public User answeringTo;

    @ManyToOne
    @JoinColumn(name = "fk_comment_id", updatable = false, insertable = false)
    public Comment comment;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getAnsweringToId() {
        return answeringToId;
    }

    public void setAnsweringToId(UUID answeringToId) {
        this.answeringToId = answeringToId;
    }

    public UUID getCommentId() {
        return commentId;
    }

    public void setCommentId(UUID commentId) {
        this.commentId = commentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getAnsweringTo() {
        return answeringTo;
    }

    public void setAnsweringTo(User answeringTo) {
        this.answeringTo = answeringTo;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
