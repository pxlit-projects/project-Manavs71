package org.example.service;

import org.example.domain.Comment;
import org.example.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // Create a new comment
    public Comment createComment(Long postId, String author, String content) {
        Comment comment = new Comment(postId, author, content, LocalDateTime.now());
        return commentRepository.save(comment);
    }

    // Get all comments for a specific post
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    // Update an existing comment
    public Optional<Comment> updateComment(Long id, String content) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            comment.get().setContent(content);
            comment.get().setCreatedDate(LocalDateTime.now());
            return Optional.of(commentRepository.save(comment.get()));
        }
        return Optional.empty();
    }

    // Delete a comment by ID
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
