package org.example;

import org.example.controller.ReviewController;
import org.example.domain.Review;
import org.example.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewControllerTests {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void approvePostReturnsApprovedReview() {
        Long postId = 1L;
        String reviewer = "Reviewer";
        Review review = new Review();
        review.setPostId(postId);
        review.setStatus("APPROVED");
        review.setReviewer(reviewer);

        when(reviewService.approvePost(postId, reviewer)).thenReturn(review);

        ResponseEntity<?> response = reviewController.approvePost(postId, reviewer);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(review, response.getBody());
    }

    @Test
    void approvePostReturnsConflictOnIllegalStateException() {
        Long postId = 1L;
        String reviewer = "Reviewer";

        when(reviewService.approvePost(postId, reviewer)).thenThrow(new IllegalStateException("Conflict"));

        ResponseEntity<?> response = reviewController.approvePost(postId, reviewer);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflict", response.getBody());
    }

    @Test
    void approvePostReturnsInternalServerErrorOnException() {
        Long postId = 1L;
        String reviewer = "Reviewer";

        when(reviewService.approvePost(postId, reviewer)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = reviewController.approvePost(postId, reviewer);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error", response.getBody());
    }

    @Test
    void rejectPostReturnsRejectedReview() {
        Long postId = 1L;
        String reviewer = "Reviewer";
        String comment = "Needs improvement";
        Review review = new Review();
        review.setPostId(postId);
        review.setStatus("REJECTED");
        review.setReviewer(reviewer);
        review.setComment(comment);

        when(reviewService.rejectPost(postId, reviewer, comment)).thenReturn(review);

        ResponseEntity<?> response = reviewController.rejectPost(postId, reviewer, comment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(review, response.getBody());
    }

    @Test
    void rejectPostReturnsConflictOnIllegalStateException() {
        Long postId = 1L;
        String reviewer = "Reviewer";
        String comment = "Needs improvement";

        when(reviewService.rejectPost(postId, reviewer, comment)).thenThrow(new IllegalStateException("Conflict"));

        ResponseEntity<?> response = reviewController.rejectPost(postId, reviewer, comment);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflict", response.getBody());
    }

    @Test
    void rejectPostReturnsInternalServerErrorOnException() {
        Long postId = 1L;
        String reviewer = "Reviewer";
        String comment = "Needs improvement";

        when(reviewService.rejectPost(postId, reviewer, comment)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = reviewController.rejectPost(postId, reviewer, comment);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred.", response.getBody());
    }
}