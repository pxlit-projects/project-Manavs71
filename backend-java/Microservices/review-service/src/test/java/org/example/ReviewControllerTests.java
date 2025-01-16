package org.example;

import org.example.Exception.ReviewServiceException;
import org.example.domain.Review;
import org.example.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ReviewControllerTests {

    @Container
    private static final MySQLContainer<?> sqlContainer = new MySQLContainer<>("mysql:5.7.37");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Test
    void approvePostReturnsOkWhenSuccessful() throws Exception {
        // Given
        Long postId = 1L;
        String reviewer = "Reviewer";
        Review approvedReview = new Review();
        approvedReview.setPostId(postId);
        approvedReview.setStatus("APPROVED");
        approvedReview.setReviewer(reviewer);
        approvedReview.setReviewDate(LocalDateTime.now());

        when(reviewService.approvePost(postId, reviewer)).thenReturn(approvedReview);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/review/approve/{postId}", postId)
                        .param("reviewer", reviewer))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("APPROVED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reviewer").value(reviewer));
    }

    @Test
    void approvePostReturnsInternalServerErrorWhenReviewServiceException() throws Exception {
        // Given
        Long postId = 1L;
        String reviewer = "Reviewer";

        when(reviewService.approvePost(postId, reviewer)).thenThrow(new ReviewServiceException("Error"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/review/approve/{postId}", postId)
                        .param("reviewer", reviewer))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Error approving post: Error"));
    }

    @Test
    void approvePostReturnsInternalServerErrorWhenUnexpectedError() throws Exception {
        // Given
        Long postId = 1L;
        String reviewer = "Reviewer";

        when(reviewService.approvePost(postId, reviewer)).thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/review/approve/{postId}", postId)
                        .param("reviewer", reviewer))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("An unexpected error occurred while approving the post."));
    }

    @Test
    void rejectPostReturnsOkWhenSuccessful() throws Exception {
        // Given
        Long postId = 1L;
        String reviewer = "Reviewer";
        String comment = "Needs improvement";
        Review rejectedReview = new Review();
        rejectedReview.setPostId(postId);
        rejectedReview.setStatus("REJECTED");
        rejectedReview.setReviewer(reviewer);
        rejectedReview.setComment(comment);
        rejectedReview.setReviewDate(LocalDateTime.now());

        when(reviewService.rejectPost(postId, reviewer, comment)).thenReturn(rejectedReview);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/review/reject/{postId}", postId)
                        .param("reviewer", reviewer)
                        .param("comment", comment))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("REJECTED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reviewer").value(reviewer))
                .andExpect(MockMvcResultMatchers.jsonPath("$.comment").value(comment));
    }

    @Test
    void rejectPostReturnsInternalServerErrorWhenReviewServiceException() throws Exception {
        // Given
        Long postId = 1L;
        String reviewer = "Reviewer";
        String comment = "Needs improvement";

        when(reviewService.rejectPost(postId, reviewer, comment)).thenThrow(new ReviewServiceException("Error"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/review/reject/{postId}", postId)
                        .param("reviewer", reviewer)
                        .param("comment", comment))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Error rejecting post: Error"));
    }

    @Test
    void rejectPostReturnsInternalServerErrorWhenUnexpectedError() throws Exception {
        // Given
        Long postId = 1L;
        String reviewer = "Reviewer";
        String comment = "Needs improvement";

        when(reviewService.rejectPost(postId, reviewer, comment)).thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/review/reject/{postId}", postId)
                        .param("reviewer", reviewer)
                        .param("comment", comment))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("An unexpected error occurred while rejecting the post."));
    }
}
