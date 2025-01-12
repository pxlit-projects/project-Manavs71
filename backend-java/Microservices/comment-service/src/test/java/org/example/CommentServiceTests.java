package org.example;

import org.example.domain.Comment;
import org.example.repository.CommentRepository;
import org.example.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTests {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCommentReturnsSavedComment() {
        Long postId = 1L;
        String author = "Author";
        String content = "Content";
        Comment comment = new Comment(postId, author, content, LocalDateTime.now());

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.createComment(postId, author, content);

        assertEquals(postId, result.getPostId());
        assertEquals(author, result.getUsername());
        assertEquals(content, result.getContent());
        assertNotNull(result.getCreatedDate());
    }

    @Test
    void getCommentsByPostIdReturnsListOfComments() {
        Long postId = 1L;
        Comment comment = new Comment(postId, "Author", "Content", LocalDateTime.now());

        when(commentRepository.findByPostId(postId)).thenReturn(Collections.singletonList(comment));

        List<Comment> result = commentService.getCommentsByPostId(postId);

        assertEquals(1, result.size());
        assertEquals(comment, result.get(0));
    }

    @Test
    void updateCommentReturnsUpdatedComment() {
        Long commentId = 1L;
        String newContent = "Updated Content";
        Comment comment = new Comment(1L, "Author", "Content", LocalDateTime.now());

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Optional<Comment> result = commentService.updateComment(commentId, newContent);

        assertTrue(result.isPresent());
        assertEquals(newContent, result.get().getContent());
        assertNotNull(result.get().getCreatedDate());
    }

    @Test
    void updateCommentReturnsEmptyIfNotFound() {
        Long commentId = 1L;
        String newContent = "Updated Content";

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        Optional<Comment> result = commentService.updateComment(commentId, newContent);

        assertFalse(result.isPresent());
    }

    @Test
    void deleteCommentRemovesComment() {
        Long commentId = 1L;

        doNothing().when(commentRepository).deleteById(commentId);

        commentService.deleteComment(commentId);

        verify(commentRepository, times(1)).deleteById(commentId);
    }
}