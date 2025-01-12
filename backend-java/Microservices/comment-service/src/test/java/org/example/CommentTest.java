package org.example;

import org.example.domain.Comment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void createCommentWithValidData() {
        Comment comment = new Comment(1L, "Author", "Content", LocalDateTime.now());
        assertEquals(1L, comment.getPostId());
        assertEquals("Author", comment.getUsername());
        assertEquals("Content", comment.getContent());
        assertNotNull(comment.getCreatedDate());
    }

    @Test
    void createCommentWithNullContent() {
        Comment comment = new Comment(1L, "Author", null, LocalDateTime.now());
        assertNull(comment.getContent());
    }

    @Test
    void createCommentWithEmptyAuthor() {
        Comment comment = new Comment(1L, "", "Content", LocalDateTime.now());
        assertEquals("", comment.getUsername());
    }

    @Test
    void createCommentWithFutureCreatedDate() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        Comment comment = new Comment(1L, "Author", "Content", futureDate);
        assertEquals(futureDate, comment.getCreatedDate());
    }

    @Test
    void setAndGetPostId() {
        Comment comment = new Comment();
        comment.setPostId(1L);
        assertEquals(1L, comment.getPostId());
    }

    @Test
    void setAndGetAuthor() {
        Comment comment = new Comment();
        comment.setUsername("Author");
        assertEquals("Author", comment.getUsername());
    }

    @Test
    void setAndGetContent() {
        Comment comment = new Comment();
        comment.setContent("Content");
        assertEquals("Content", comment.getContent());
    }

    @Test
    void setAndGetCreatedDate() {
        Comment comment = new Comment();
        LocalDateTime now = LocalDateTime.now();
        comment.setCreatedDate(now);
        assertEquals(now, comment.getCreatedDate());
    }
}