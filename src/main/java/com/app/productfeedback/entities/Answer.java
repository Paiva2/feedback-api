package com.app.productfeedback.entities;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

@Entity(name = "tb_answers")
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
    private String userId;

    @Column(name = "fk_answering_to", nullable = false)
    private String answeringToId;

    @Column(name = "fk_comment_id", nullable = false)
    private String commentId;

    @ManyToOne
    @JoinColumn(name = "fk_user_id", updatable = false, insertable = false)
    public Comment user;

    @ManyToOne
    @JoinColumn(name = "fk_answering_to", updatable = false, insertable = false)
    public Comment answeringTo;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAnsweringToId() {
        return answeringToId;
    }

    public void setAnsweringToId(String answeringToId) {
        this.answeringToId = answeringToId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Comment getUser() {
        return user;
    }

    public void setUser(Comment user) {
        this.user = user;
    }

    public Comment getAnsweringTo() {
        return answeringTo;
    }

    public void setAnsweringTo(Comment answeringTo) {
        this.answeringTo = answeringTo;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
