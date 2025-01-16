package org.example;

import org.example.DTO.CommentDTO;
import org.example.DTO.PostResponseDTO;
import org.example.domain.PostStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class PostResponseDTOTests {

    @Container
    private static MySQLContainer<?> sqlContainer = new MySQLContainer<>("mysql:5.7.37");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @Test
    void postResponseDTOHandlesNullComments() {
        PostResponseDTO postResponseDTO = new PostResponseDTO(1L, "Title", "Content", "Author", LocalDateTime.now(), false, PostStatus.PENDING, "Rejection comment");
        postResponseDTO.setComments(null);
        assertNull(postResponseDTO.getComments());
    }

    @Test
    void postResponseDTOHandlesEmptyComments() {
        PostResponseDTO postResponseDTO = new PostResponseDTO(1L, "Title", "Content", "Author", LocalDateTime.now(), false, PostStatus.PENDING, "Rejection comment");
        postResponseDTO.setComments(Collections.emptyList());
        assertTrue(postResponseDTO.getComments().isEmpty());
    }

    @Test
    void postResponseDTOHandlesNonEmptyComments() {
        CommentDTO comment = new CommentDTO(1L, "Comment content", "Comment author", LocalDateTime.now());
        PostResponseDTO postResponseDTO = new PostResponseDTO(1L, "Title", "Content", "Author", LocalDateTime.now(), false, PostStatus.PENDING, "Rejection comment");
        postResponseDTO.setComments(Collections.singletonList(comment));
        assertEquals(1, postResponseDTO.getComments().size());
        assertEquals(comment, postResponseDTO.getComments().get(0));
    }

    @Test
    void postResponseDTOHandlesRejectionComment() {
        PostResponseDTO postResponseDTO = new PostResponseDTO(1L, "Title", "Content", "Author", LocalDateTime.now(), false, PostStatus.REJECTED, "Rejection comment");
        assertEquals("Rejection comment", postResponseDTO.getRejectionComment());
        postResponseDTO.setRejectionComment("Updated rejection comment");
        assertEquals("Updated rejection comment", postResponseDTO.getRejectionComment());
    }
}