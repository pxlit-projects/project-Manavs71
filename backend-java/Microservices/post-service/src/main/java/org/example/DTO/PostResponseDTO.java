package org.example.DTO;

import org.example.domain.PostStatus;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDTO {

    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdDate;
    private boolean isDraft;

    private PostStatus postStatus;

    private List<CommentDTO> comments; // New field for comments

    private String rejectionComment;

    // Constructor
    public PostResponseDTO(Long id, String title, String content, String author, LocalDateTime createdDate, boolean isDraft, PostStatus postStatus, String rejectionComment) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdDate = createdDate;
        this.isDraft = isDraft;
        this.postStatus = postStatus;
        this.rejectionComment = rejectionComment;
    }

    public String getRejectionComment() {
        return rejectionComment;
    }

    public void setRejectionComment(String rejectionComment) {
        this.rejectionComment = rejectionComment;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    // Getters and Setters


    public String getTitle() {
        return title;
    }


    public String getContent() {
        return content;
    }


    public String getAuthor() {
        return author;
    }


    public boolean isDraft() {
        return isDraft;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setDraft(boolean draft) {
        isDraft = draft;
    }

    public PostStatus getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }
}
