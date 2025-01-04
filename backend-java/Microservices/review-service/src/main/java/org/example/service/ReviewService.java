package org.example.service;

import org.example.domain.Review;
import org.example.reviewRepository.ReviewRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final RabbitTemplate rabbitTemplate;

    public ReviewService(RabbitTemplate rabbitTemplate, ReviewRepository reviewRepository){
        this.reviewRepository = reviewRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Review approvePost(Long postId, String reviewer) {
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

        // Send rejection message to RabbitMQ

        rabbitTemplate.convertAndSend("post_review_exchange", "post.reject", "Post ID " + postId + " rejected. Comment: " + comment);

        //for debug purpose
        System.out.println("The rejected message for post id: "+ postId +" is sent");
        return review;
    }
}
