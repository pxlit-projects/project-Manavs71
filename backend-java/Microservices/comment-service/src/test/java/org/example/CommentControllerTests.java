package org.example;

import org.example.Controller.CommentController;
import org.example.domain.Comment;
import org.example.repository.CommentRepository;
import org.example.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class CommentControllerTests {

    @Container
    private static MySQLContainer<?> sqlContainer = new MySQLContainer<>("mysql:5.7.37");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll(); // Clean database before each test
    }

    @Test
    void createCommentReturnsCreatedStatus() {
        Long postId = 1L;
        String username = "Author";
        String content = "Content";

        ResponseEntity<Comment> response = commentController.createComment(postId, username, content);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(postId, response.getBody().getPostId());
        assertEquals(username, response.getBody().getUsername());
        assertEquals(content, response.getBody().getContent());
    }

    @Test
    void getCommentsByPostIdReturnsListOfComments() {
        Long postId = 1L;
        Comment comment = new Comment(postId, "Author", "Content", LocalDateTime.now());
        commentRepository.save(comment);

        ResponseEntity<List<Comment>> response = commentController.getCommentsByPostId(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(comment.getPostId(), response.getBody().get(0).getPostId());
    }

    @Test
    void updateCommentReturnsUpdatedComment() {
        Comment comment = new Comment(1L, "Author", "Content", LocalDateTime.now());
        comment = commentRepository.save(comment);

        String newContent = "Updated Content";
        ResponseEntity<Comment> response = commentController.updateComment(comment.getId(), newContent);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newContent, response.getBody().getContent());
    }

    @Test
    void updateCommentReturnsNotFoundIfCommentDoesNotExist() {
        Long commentId = 999L; // Non-existent ID
        String newContent = "Updated Content";

        ResponseEntity<Comment> response = commentController.updateComment(commentId, newContent);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteCommentReturnsNoContentStatus() {
        Comment comment = new Comment(1L, "Author", "Content", LocalDateTime.now());
        comment = commentRepository.save(comment);

        ResponseEntity<Void> response = commentController.deleteComment(comment.getId());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(commentRepository.findById(comment.getId()).isPresent());
    }
}
