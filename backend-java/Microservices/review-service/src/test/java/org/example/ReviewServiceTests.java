package org.example;

import org.example.Exception.ReviewServiceException;
import org.example.clients.PostServiceClient;
import org.example.domain.Review;
import org.example.reviewRepository.ReviewRepository;
import org.example.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@Testcontainers
class ReviewServiceTests {

    @Container
    private static final MySQLContainer<?> sqlContainer = new MySQLContainer<>("mysql:5.7.37");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @Autowired
    private ReviewRepository reviewRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private PostServiceClient postServiceClient;

    @Autowired
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll(); // Clean up before each test
    }

    @Test
    void approvePostSavesApprovedReview() {
        // Given
        Long postId = 1L;
        String reviewer = "Reviewer";
        Review review = new Review();
        review.setPostId(postId);
        review.setStatus("APPROVED");
        review.setReviewer(reviewer);
        review.setReviewDate(LocalDateTime.now());
        Review savedReview  =reviewRepository.save(review);
        // When
        Review result = reviewService.approvePost(postId, reviewer);

        // Then
        assertNotNull(savedReview);
        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus());
        assertEquals(reviewer, result.getReviewer());
        assertNotNull(result.getReviewDate());
        verify(rabbitTemplate, times(1)).convertAndSend("post_review_exchange", "post.approve", "Post ID " + postId + " approved.");
    }

    @Test
    void rejectPostSavesRejectedReview() {
        // Given
        Long postId = 1L;
        String reviewer = "Reviewer";
        String comment = "Needs improvement";
        Review review = new Review();
        review.setPostId(postId);
        review.setStatus("REJECTED");
        review.setReviewer(reviewer);
        review.setComment(comment);
        review.setReviewDate(LocalDateTime.now());

        // When
        Review result = reviewService.rejectPost(postId, reviewer, comment);

        // Then
        assertNotNull(result);
        assertEquals("REJECTED", result.getStatus());
        assertEquals(reviewer, result.getReviewer());
        assertEquals(comment, result.getComment());
        assertNotNull(result.getReviewDate());
        verify(rabbitTemplate, times(1)).convertAndSend("post_review_exchange", "post.reject", "Post ID " + postId + " rejected. Comment: " + comment);
        verify(postServiceClient, times(1)).updateRejectionComment(postId, comment);
    }

    @Test
    void approvePostHandlesExistingReviews() {
        // Given
        Long postId = 1L;
        String reviewer = "Reviewer";
        Review existingReview = new Review();
        existingReview.setPostId(postId);
        existingReview.setStatus("REJECTED");
        existingReview.setReviewer("Old Reviewer");
        existingReview.setReviewDate(LocalDateTime.now().minusDays(1));
        reviewRepository.save(existingReview);

        // When
        Review result = reviewService.approvePost(postId, reviewer);

        // Then
        assertEquals("APPROVED", result.getStatus());
        assertEquals(reviewer, result.getReviewer());
        assertNotNull(result.getReviewDate());
        verify(rabbitTemplate, times(1)).convertAndSend("post_review_exchange", "post.approve", "Post ID " + postId + " approved.");
    }

    @Test
    void approvePostThrowsExceptionWhenNoReviewsFound() {
        // Given
        Long postId = 1L;
        String reviewer = "Reviewer";

        // When & Then
        assertThrows(ReviewServiceException.class, () -> reviewService.approvePost(postId, reviewer));
    }

    @Test
    void approvePostThrowsExceptionWhenSaveFails() {
        // Given
        Long postId = 1L;
        String reviewer = "Reviewer";
        Review review = new Review();
        review.setPostId(postId);
        review.setStatus("APPROVED");
        review.setReviewer(reviewer);
        review.setReviewDate(LocalDateTime.now());

        // Simulate a failure in the save operation
        reviewRepository = mock(ReviewRepository.class);

        doThrow(new RuntimeException("Database error")).when(reviewRepository).save(any(Review.class));

        // When & Then
        assertThrows(ReviewServiceException.class, () -> reviewService.approvePost(postId, reviewer));
    }

    @Test
    void rejectPostThrowsExceptionWhenRabbitMQFails() {
        // Given
        Long postId = 1L;
        String reviewer = "Reviewer";
        String comment = "Needs improvement";
        Review review = new Review();
        review.setPostId(postId);
        review.setStatus("REJECTED");
        review.setReviewer(reviewer);
        review.setComment(comment);
        review.setReviewDate(LocalDateTime.now());

        // Simulate RabbitMQ failure
        doThrow(new RuntimeException("RabbitMQ error")).when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());

        // When & Then
        assertThrows(ReviewServiceException.class, () -> reviewService.rejectPost(postId, reviewer, comment));
    }

    @Test
    void rejectPostThrowsExceptionWhenExternalServiceFails() {
        // Given
        Long postId = 1L;
        String reviewer = "Reviewer";
        String comment = "Needs improvement";
        Review review = new Review();
        review.setPostId(postId);
        review.setStatus("REJECTED");
        review.setReviewer(reviewer);
        review.setComment(comment);
        review.setReviewDate(LocalDateTime.now());

        // Simulate external service failure
        doThrow(new RuntimeException("External service error")).when(postServiceClient).updateRejectionComment(anyLong(), anyString());

        // When & Then
        assertThrows(ReviewServiceException.class, () -> reviewService.rejectPost(postId, reviewer, comment));
    }

    @Test
    void rejectPostWithEmptyComment() {
        // Given
        Long postId = 1L;
        String reviewer = "Reviewer";
        String comment = "";
        Review review = new Review();
        review.setPostId(postId);
        review.setStatus("REJECTED");
        review.setReviewer(reviewer);
        review.setComment(comment);
        review.setReviewDate(LocalDateTime.now());

        // When
        Review result = reviewService.rejectPost(postId, reviewer, comment);

        // Then
        assertNotNull(result);
        assertEquals("REJECTED", result.getStatus());
        assertEquals(reviewer, result.getReviewer());
        assertEquals(comment, result.getComment());
        assertNotNull(result.getReviewDate());
        verify(rabbitTemplate, times(1)).convertAndSend("post_review_exchange", "post.reject", "Post ID " + postId + " rejected. Comment: " + comment);
        verify(postServiceClient, times(1)).updateRejectionComment(postId, comment);
    }

    @Test
    void approvePostWithInvalidPostId() {
        // Given
        Long postId = -1L;  // Invalid post ID
        String reviewer = "Reviewer";

        // When & Then
        assertThrows(ReviewServiceException.class, () -> reviewService.approvePost(postId, reviewer));
    }
}
