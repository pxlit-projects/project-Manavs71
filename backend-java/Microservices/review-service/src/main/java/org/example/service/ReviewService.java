package org.example.service;

import org.example.Exception.ReviewServiceException;
import org.example.clients.PostServiceClient;
import org.example.domain.Review;
import org.example.reviewRepository.ReviewRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RabbitTemplate rabbitTemplate;
    private final PostServiceClient postServiceClient;

    @Autowired
    public ReviewService(RabbitTemplate rabbitTemplate, ReviewRepository reviewRepository, PostServiceClient postServiceClient) {
        this.reviewRepository = reviewRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.postServiceClient = postServiceClient;
    }

    public Review approvePost(Long postId, String reviewer) {
        // Fetch all reviews for the given post
        List<Review> existingReviews = reviewRepository.findReviewsByPostId(postId);


        // Get the latest review
        Optional<Review> latestReview = existingReviews.stream()
                .max((review1, review2) -> review1.getReviewDate().compareTo(review2.getReviewDate()));

        Review review = new Review();
        review.setPostId(postId);
        review.setStatus("APPROVED");
        review.setReviewer(reviewer);
        review.setReviewDate(LocalDateTime.now());

        try {
            reviewRepository.save(review);
        } catch (Exception e) {
            throw new ReviewServiceException("Failed to save review for post ID " + postId, e);
        }

        try {
            // Send approval message to RabbitMQ
            rabbitTemplate.convertAndSend("post_review_exchange", "post.approve", "Post ID " + postId + " approved.");
        } catch (Exception e) {
            throw new ReviewServiceException("Failed to send approval message to RabbitMQ for post ID " + postId, e);
        }

        // Debug purpose
        System.out.println("The approved message for post ID: " + postId + " is sent");

        return review;
    }

    public Review rejectPost(Long postId, String reviewer, String comment) {
        Review review = new Review();
        review.setPostId(postId);
        review.setStatus("REJECTED");
        review.setReviewer(reviewer);
        review.setComment(comment);
        review.setReviewDate(LocalDateTime.now());

        try {
            reviewRepository.save(review);
        } catch (Exception e) {
            throw new ReviewServiceException("Failed to save rejection review for post ID " + postId, e);
        }

        try {
            // Update rejection comment via external service
            postServiceClient.updateRejectionComment(postId, comment);
        } catch (Exception ex) {
            throw new ReviewServiceException("Failed to update rejection comment for post ID " + postId, ex);
        }

        try {
            // Send rejection message to RabbitMQ
            rabbitTemplate.convertAndSend("post_review_exchange", "post.reject", "Post ID " + postId + " rejected. Comment: " + comment);
        } catch (Exception e) {
            throw new ReviewServiceException("Failed to send rejection message to RabbitMQ for post ID " + postId, e);
        }

        // Debug purpose
        System.out.println("The rejected message for post ID: " + postId + " is sent");

        return review;
    }
}
