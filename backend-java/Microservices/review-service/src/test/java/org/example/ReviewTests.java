package org.example;

import org.example.domain.Review;
import org.example.reviewRepository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class ReviewTests {

    // MySQL Container for integration tests
    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:5.7.37");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Autowired
    private ReviewRepository reviewRepository;  // Assuming ReviewRepository exists to interact with the DB

    @BeforeEach
    void setUp() {
        // Any setup for tests, like clearing the database or preparing data
        reviewRepository.deleteAll();
    }

    @Test
    void createReviewWithValidData() {
        Review review = new Review();
        review.setPostId(1L);
        review.setStatus("Approved");
        review.setReviewer("Reviewer");
        review.setComment("Good post");
        review.setReviewDate(LocalDateTime.now());

        // Save to DB (use ReviewRepository if you're using JPA)
        Review savedReview = reviewRepository.save(review);

        // Ensure the review was saved and has a valid ID
        assertNotNull(savedReview);
        assertNotNull(savedReview.getId());  // Ensure ID is populated after save

        // Fetch the review from the DB by ID
        Review fetchedReview = reviewRepository.findById(savedReview.getId()).orElseThrow(() ->
                new AssertionError("Review not found with ID: " + savedReview.getId()));

        // Assert the saved and fetched reviews are equal
        assertEquals(savedReview.getId(), fetchedReview.getId());
        assertEquals(savedReview.getPostId(), fetchedReview.getPostId());
        assertEquals(savedReview.getStatus(), fetchedReview.getStatus());
        assertEquals(savedReview.getReviewer(), fetchedReview.getReviewer());
        assertEquals(savedReview.getComment(), fetchedReview.getComment());
        assertNotNull(fetchedReview.getReviewDate());
    }

    @Test
    void createReviewWithNullComment() {
        // Arrange
        Review review = new Review();
        review.setComment(null);

        // Save to DB
        reviewRepository.save(review);

        // Assert
        Review fetchedReview = reviewRepository.findById(review.getId()).orElseThrow();
        assertNull(fetchedReview.getComment());
    }

    @Test
    void createReviewWithEmptyStatus() {
        // Arrange
        Review review = new Review();
        review.setStatus("");

        // Save to DB
        reviewRepository.save(review);

        // Assert
        Review fetchedReview = reviewRepository.findById(review.getId()).orElseThrow();
        assertEquals("", fetchedReview.getStatus());
    }

    @Test
    void createReviewWithFutureReviewDate() {
        // Arrange
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1).truncatedTo(java.time.temporal.ChronoUnit.SECONDS);  // Round to seconds
        Review review = new Review();
        review.setReviewDate(futureDate);

        // Save to DB
        reviewRepository.save(review);

        // Assert
        Review fetchedReview = reviewRepository.findById(review.getId()).orElseThrow();

        // Round the fetched review's reviewDate to match the precision level
        assertEquals(futureDate, fetchedReview.getReviewDate().truncatedTo(java.time.temporal.ChronoUnit.SECONDS));
    }

    @Test
    void setAndGetPostId() {
        // Arrange
        Review review = new Review();
        review.setPostId(1L);

        // Assert
        assertEquals(1L, review.getPostId());
    }

    @Test
    void setAndGetReviewer() {
        // Arrange
        Review review = new Review();
        review.setReviewer("Reviewer");

        // Assert
        assertEquals("Reviewer", review.getReviewer());
    }

    @Test
    void setAndGetStatus() {
        // Arrange
        Review review = new Review();
        review.setStatus("Approved");

        // Assert
        assertEquals("Approved", review.getStatus());
    }

    @Test
    void setAndGetComment() {
        // Arrange
        Review review = new Review();
        review.setComment("Good post");

        // Assert
        assertEquals("Good post", review.getComment());
    }

    @Test
    void setAndGetReviewDate() {
        // Arrange
        Review review = new Review();
        LocalDateTime now = LocalDateTime.now();
        review.setReviewDate(now);

        // Assert
        assertEquals(now, review.getReviewDate());
    }
}
