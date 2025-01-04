package org.example.controller;

import org.example.domain.Review;
import org.example.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/approve/{postId}")
    public ResponseEntity<Review> approvePost(
            @PathVariable Long postId,
            @RequestParam String reviewer) {
        Review approvedReview = reviewService.approvePost(postId, reviewer);
        return ResponseEntity.ok(approvedReview);
    }


    @PostMapping("/reject/{postId}")
    public ResponseEntity<Review> rejectPost(
            @PathVariable Long postId,
            @RequestParam String reviewer,
            @RequestParam String comment) {
        Review rejectedReview = reviewService.rejectPost(postId, reviewer, comment);
        return ResponseEntity.ok(rejectedReview);
    }

}