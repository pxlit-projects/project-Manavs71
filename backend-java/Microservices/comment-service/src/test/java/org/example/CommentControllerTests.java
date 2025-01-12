package org.example;

import org.example.Controller.CommentController;
import org.example.domain.Comment;
import org.example.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentControllerTests {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCommentReturnsCreatedStatus() {
        Long postId = 1L;
        String username = "Author";
        String content = "Content";
        Comment comment = new Comment(postId, username, content, LocalDateTime.now());

        when(commentService.createComment(postId, username, content)).thenReturn(comment);

        ResponseEntity<Comment> response = commentController.createComment(postId, username, content);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(comment, response.getBody());
    }

    @Test
    void getCommentsByPostIdReturnsListOfComments() {
        Long postId = 1L;
        Comment comment = new Comment(postId, "Author", "Content", LocalDateTime.now());

        when(commentService.getCommentsByPostId(postId)).thenReturn(Collections.singletonList(comment));

        ResponseEntity<List<Comment>> response = commentController.getCommentsByPostId(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(comment, response.getBody().get(0));
    }

    @Test
    void updateCommentReturnsUpdatedComment() {
        Long commentId = 1L;
        String newContent = "Updated Content";
        Comment comment = new Comment(1L, "Author", "Content", LocalDateTime.now());

        when(commentService.updateComment(commentId, newContent)).thenReturn(Optional.of(comment));

        ResponseEntity<Comment> response = commentController.updateComment(commentId, newContent);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comment, response.getBody());
    }

    @Test
    void updateCommentReturnsNotFoundIfCommentDoesNotExist() {
        Long commentId = 1L;
        String newContent = "Updated Content";

        when(commentService.updateComment(commentId, newContent)).thenReturn(Optional.empty());

        ResponseEntity<Comment> response = commentController.updateComment(commentId, newContent);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteCommentReturnsNoContentStatus() {
        Long commentId = 1L;

        doNothing().when(commentService).deleteComment(commentId);

        ResponseEntity<Void> response = commentController.deleteComment(commentId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(commentService, times(1)).deleteComment(commentId);
    }
}