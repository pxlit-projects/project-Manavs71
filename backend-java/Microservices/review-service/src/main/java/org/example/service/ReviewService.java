package org.example.service;

import org.antlr.v4.runtime.ConsoleErrorListener;
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

    public ReviewService(RabbitTemplate rabbitTemplate, ReviewRepository reviewRepository, PostServiceClient postServiceClient){
        this.reviewRepository = reviewRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.postServiceClient = postServiceClient;
    }

    public Review approvePost(Long postId, String reviewer) {

        // Fetch all reviews for the given post
        List<Review> existingReviews = reviewRepository.findReviewsByPostId(postId);

        // If there are any reviews, get the latest one by sorting by review_date DESC
        Optional<Review> latestReview = existingReviews.stream()
                .max((review1, review2) -> review1.getReviewDate().compareTo(review2.getReviewDate()));


        Review review = new Review();
        review.setPostId(postId);
        review.setStatus("APPROVED");
        review.setReviewer(reviewer);
        review.setReviewDate(LocalDateTime.now());
        reviewRepository.save(review);

        // Send approval message to RabbitMQ
        rabbitTemplate.convertAndSend("post_review_exchange", "post.approve", "Post ID " + postId + " approved.");

        //for debug purpose
        System.out.println("The approved message for post id: "+ postId +" is sent");

        return review;
    }

    public Review rejectPost(Long postId, String reviewer, String comment) {
        Review review = new Review();
        review.setPostId(postId);
        review.setStatus("REJECTED");
        review.setReviewer(reviewer);
        review.setComment(comment);
        review.setReviewDate(LocalDateTime.now());
        reviewRepository.save(review);

        try
        {
            postServiceClient.updateRejectionComment(postId, comment);

        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        rabbitTemplate.convertAndSend("post_review_exchange", "post.reject", "Post ID " + postId + " rejected. Comment: " + comment);



        //for debug purpose
        System.out.println("The rejected message for post id: "+ postId +" is sent");
        return review;
    }
}
