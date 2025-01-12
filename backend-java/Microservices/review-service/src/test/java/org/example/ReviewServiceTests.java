package org.example;

import org.example.clients.PostServiceClient;
import org.example.domain.Review;
import org.example.reviewRepository.ReviewRepository;
import org.example.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTests {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void approvePostSavesApprovedReview() {
        Long postId = 1L;
        String reviewer = "Reviewer";
        Review review = new Review();
        review.setPostId(postId);
        review.setStatus("APPROVED");
        review.setReviewer(reviewer);
        review.setReviewDate(LocalDateTime.now());

        when(reviewRepository.findReviewsByPostId(postId)).thenReturn(Collections.emptyList());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.approvePost(postId, reviewer);

        assertEquals("APPROVED", result.getStatus());
        assertEquals(reviewer, result.getReviewer());
        assertNotNull(result.getReviewDate());
        verify(rabbitTemplate, times(1)).convertAndSend("post_review_exchange", "post.approve", "Post ID " + postId + " approved.");
    }

    @Test
    void rejectPostSavesRejectedReview() {
        Long postId = 1L;
        String reviewer = "Reviewer";
        String comment = "Needs improvement";
        Review review = new Review();
        review.setPostId(postId);
        review.setStatus("REJECTED");
        review.setReviewer(reviewer);
        review.setComment(comment);
        review.setReviewDate(LocalDateTime.now());

        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.rejectPost(postId, reviewer, comment);

        assertEquals("REJECTED", result.getStatus());
        assertEquals(reviewer, result.getReviewer());
        assertEquals(comment, result.getComment());
        assertNotNull(result.getReviewDate());
        verify(rabbitTemplate, times(1)).convertAndSend("post_review_exchange", "post.reject", "Post ID " + postId + " rejected. Comment: " + comment);
    }

    @Test
    void approvePostHandlesExistingReviews() {
        Long postId = 1L;
        String reviewer = "Reviewer";
        Review existingReview = new Review();
        existingReview.setPostId(postId);
        existingReview.setStatus("REJECTED");
        existingReview.setReviewer("Old Reviewer");
        existingReview.setReviewDate(LocalDateTime.now().minusDays(1));

        when(reviewRepository.findReviewsByPostId(postId)).thenReturn(Collections.singletonList(existingReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(existingReview);

        Review result = reviewService.approvePost(postId, reviewer);

        assertEquals("APPROVED", result.getStatus());
        assertEquals(reviewer, result.getReviewer());
        assertNotNull(result.getReviewDate());
        verify(rabbitTemplate, times(1)).convertAndSend("post_review_exchange", "post.approve", "Post ID " + postId + " approved.");
    }

    @Test
    void rejectPostHandlesExceptionFromPostServiceClient() {
        Long postId = 1L;
        String reviewer = "Reviewer";
        String comment = "Needs improvement";
        Review review = new Review();
        review.setPostId(postId);
        review.setStatus("REJECTED");
        review.setReviewer(reviewer);
        review.setComment(comment);
        review.setReviewDate(LocalDateTime.now());
        PostServiceClient postServiceClient = mock(PostServiceClient.class);

        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        doThrow(new RuntimeException("Post service unavailable")).when(postServiceClient).updateRejectionComment(postId, comment);

        Review result = reviewService.rejectPost(postId, reviewer, comment);

        assertEquals("REJECTED", result.getStatus());
        assertEquals(reviewer, result.getReviewer());
        assertEquals(comment, result.getComment());
        assertNotNull(result.getReviewDate());
        verify(rabbitTemplate, times(1)).convertAndSend("post_review_exchange", "post.reject", "Post ID " + postId + " rejected. Comment: " + comment);
    }
}