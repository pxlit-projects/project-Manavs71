package org.example;

import org.example.domain.Comment;
import org.example.repository.CommentRepository;
import org.example.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
class CommentServiceTests {

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

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll(); // Clean database before each test
    }

    @Test
    void createCommentReturnsSavedComment() {
        Long postId = 1L;
        String author = "Author";
        String content = "Content";

        Comment result = commentService.createComment(postId, author, content);

        assertNotNull(result.getId()); // ID should be auto-generated
        assertEquals(postId, result.getPostId());
        assertEquals(author, result.getUsername());
        assertEquals(content, result.getContent());
        assertNotNull(result.getCreatedDate());
    }

    @Test
    void getCommentsByPostIdReturnsListOfComments() {
        Long postId = 1L;
        Comment comment = new Comment(postId, "Author", "Content", LocalDateTime.now());
        commentRepository.save(comment);

        List<Comment> result = commentService.getCommentsByPostId(postId);

        assertEquals(1, result.size());
        assertEquals(comment.getPostId(), result.get(0).getPostId());
    }

    @Test
    void updateCommentReturnsUpdatedComment() {
        Comment comment = new Comment(1L, "Author", "Content", LocalDateTime.now());
        comment = commentRepository.save(comment);

        String newContent = "Updated Content";
        Optional<Comment> result = commentService.updateComment(comment.getId(), newContent);

        assertTrue(result.isPresent());
        assertEquals(newContent, result.get().getContent());
    }

    @Test
    void updateCommentReturnsEmptyIfNotFound() {
        Optional<Comment> result = commentService.updateComment(999L, "Updated Content");

        assertFalse(result.isPresent());
    }

    @Test
    void deleteCommentRemovesComment() {
        Comment comment = new Comment(1L, "Author", "Content", LocalDateTime.now());
        comment = commentRepository.save(comment);

        commentService.deleteComment(comment.getId());

        assertTrue(commentRepository.findById(comment.getId()).isEmpty());
    }
}
