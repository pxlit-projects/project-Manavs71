package org.example;

import org.example.domain.Review;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTests {

    @Test
    void createReviewWithValidData() {
        Review review = new Review();
        review.setId(1L);
        review.setPostId(1L);
        review.setStatus("Approved");
        review.setReviewer("Reviewer");
        review.setComment("Good post");
        review.setReviewDate(LocalDateTime.now());

        assertEquals(1L, review.getId());
        assertEquals(1L, review.getPostId());
        assertEquals("Approved", review.getStatus());
        assertEquals("Reviewer", review.getReviewer());
        assertEquals("Good post", review.getComment());
        assertNotNull(review.getReviewDate());
    }

    @Test
    void createReviewWithNullComment() {
        Review review = new Review();
        review.setComment(null);

        assertNull(review.getComment());
    }

    @Test
    void createReviewWithEmptyStatus() {
        Review review = new Review();
        review.setStatus("");

        assertEquals("", review.getStatus());
    }

    @Test
    void createReviewWithFutureReviewDate() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        Review review = new Review();
        review.setReviewDate(futureDate);

        assertEquals(futureDate, review.getReviewDate());
    }

    @Test
    void setAndGetPostId() {
        Review review = new Review();
        review.setPostId(1L);

        assertEquals(1L, review.getPostId());
    }

    @Test
    void setAndGetReviewer() {
        Review review = new Review();
        review.setReviewer("Reviewer");

        assertEquals("Reviewer", review.getReviewer());
    }

    @Test
    void setAndGetStatus() {
        Review review = new Review();
        review.setStatus("Approved");

        assertEquals("Approved", review.getStatus());
    }

    @Test
    void setAndGetComment() {
        Review review = new Review();
        review.setComment("Good post");

        assertEquals("Good post", review.getComment());
    }

    @Test
    void setAndGetReviewDate() {
        Review review = new Review();
        LocalDateTime now = LocalDateTime.now();
        review.setReviewDate(now);

        assertEquals(now, review.getReviewDate());
    }
}