package org.example;

import org.example.DTO.CommentDTO;
import org.example.PostServiceApplication;
import org.example.domain.Post;
import org.example.domain.PostStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;


class PostTest {


    @Test
    void createPostWithValidData() {
        Post post = new Post("Title", "Content", "Author");
        assertEquals("Title", post.getTitle());
        assertEquals("Content", post.getContent());
        assertEquals("Author", post.getAuthor());
        assertNotNull(post.getCreatedDate());
        assertTrue(post.isDraft());
        assertEquals(PostStatus.PENDING, post.getStatus());
        assertNull(post.getRejectionComment());
    }

    @Test
    void setAndGetComments() {
        Post post = new Post();
        post.setId(10L);
        CommentDTO comment = new CommentDTO(post.getId(), "Author", "Content", LocalDateTime.now());
        post.setComments(Collections.singletonList(comment));
        assertEquals(1, post.getComments().size());
        assertEquals(comment, post.getComments().get(0));
    }

    @Test
    void setAndGetRejectionComment() {
        Post post = new Post();
        post.setRejectionComment("Not appropriate");
        assertEquals("Not appropriate", post.getRejectionComment());
    }

    @Test
    void setAndGetStatus() {
        Post post = new Post();
        post.setStatus(PostStatus.APPROVED);
        assertEquals(PostStatus.APPROVED, post.getStatus());
    }

    @Test
    void setAndGetDraftStatus() {
        Post post = new Post();
        post.setDraft(false);
        assertFalse(post.isDraft());
    }

    @Test
    void setAndGetCreatedDate() {
        Post post = new Post();
        LocalDateTime now = LocalDateTime.now();
        post.setCreatedDate(now);
        assertEquals(now, post.getCreatedDate());
    }

    @Test
    void createPostWithEmptyTitle() {
        Post post = new Post("", "Content", "Author");
        assertEquals("", post.getTitle());
    }

    @Test
    void createPostWithNullContent() {
        Post post = new Post("Title", null, "Author");
        assertNull(post.getContent());
    }

    @Test
    void createPostWithFutureCreatedDate() {
        Post post = new Post("Title", "Content", "Author");
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        post.setCreatedDate(futureDate);
        assertEquals(futureDate, post.getCreatedDate());
    }

    @Test
    void setAndGetNullRejectionComment() {
        Post post = new Post();
        post.setRejectionComment(null);
        assertNull(post.getRejectionComment());
    }
}