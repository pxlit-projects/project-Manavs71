package org.example.controller;

import org.example.domain.Review;
import org.example.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.util.Optional;

@RestController
@RequestMapping("/review")
@CrossOrigin(origins = "http://localhost:4200")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/approve/{postId}")
    public ResponseEntity<?> approvePost(
            @PathVariable Long postId,
            @RequestParam String reviewer) {
        try {
            Review approvedReview = reviewService.approvePost(postId, reviewer);
            return ResponseEntity.ok(approvedReview);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

        }
    }

    @PostMapping("/reject/{postId}")
    public ResponseEntity<?> rejectPost(
            @PathVariable Long postId,
            @RequestParam String reviewer,
            @RequestParam String comment) {
        try {
            Review rejectedReview = reviewService.rejectPost(postId, reviewer, comment);
            return ResponseEntity.ok(rejectedReview);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
