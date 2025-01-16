package org.example;

import org.example.DTO.PostDTO;
import org.example.DTO.PostResponseDTO;
import org.example.controller.PostController;
import org.example.domain.PostStatus;
import org.example.repository.PostRepository;
import org.example.services.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class PostControllerTests {

    @Container
    private static final MySQLContainer<?> sqlContainer = new MySQLContainer<>("mysql:5.7.37");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostController postController;

    @MockBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll(); // Clean database to ensure isolation between tests
    }

    @Test
    void createPostReturnsCreatedStatus() {
        PostDTO postDTO = new PostDTO("Title", "Content", "Author");
        PostResponseDTO postResponseDTO = new PostResponseDTO(10L, "Title", "Content", "Author", LocalDateTime.now(), false, PostStatus.PENDING, "dummy rejection comment");

        when(postService.createPost(postDTO)).thenReturn(postResponseDTO);

        ResponseEntity<PostResponseDTO> response = (ResponseEntity<PostResponseDTO>) postController.createPost(postDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(postResponseDTO, response.getBody());
    }

    @Test
    void getDraftPostsReturnsListOfDrafts() {
        PostResponseDTO draftResponseDTO = new PostResponseDTO(10L, "Title", "Content", "Author", LocalDateTime.now(), true, PostStatus.PENDING, "dummy rejection comment");

        when(postService.getDraftPosts()).thenReturn(Collections.singletonList(draftResponseDTO));

        ResponseEntity<List<PostResponseDTO>> response = postController.getDraftPosts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).isDraft());
    }

    @Test
    void publishPostReturnsOkStatus() {
        Long postId = 1L;
        PostResponseDTO postResponseDTO = new PostResponseDTO(postId, "Title", "Content", "Author", LocalDateTime.now(), false, PostStatus.PENDING, "dummy rejection comment");

        when(postService.publish(postId)).thenReturn(postResponseDTO);

        ResponseEntity<PostResponseDTO> response = postController.publishPost(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postResponseDTO, response.getBody());
    }



    @Test
    void createPostWithInvalidDataReturnsBadRequest() throws Exception {
        // Invalid post data (empty title)
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/create")
                        .contentType("application/json")
                        .content("{\"title\":\"\",\"content\":\"Content\",\"author\":\"Author\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Title cannot be empty"));
    }

    @Test
    void getDraftPostsWhenNoDraftsReturnsEmptyList() throws Exception {
        // Return an empty list when there are no draft posts
        when(postService.getDraftPosts()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/drafts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }





    @Test
    void deleteDraftWhenPostNotFoundReturnsNotFound() throws Exception {
        Long nonExistingPostId = 999L;

        // Simulate no post found for the given post ID
        doThrow(new RuntimeException("Post not found")).when(postService).deleteDraft(nonExistingPostId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/draft/{postId}", nonExistingPostId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteDraftSuccessfullyRemovesDraft() throws Exception {
        Long postId = 1L;

        // Add the draft first
        PostDTO postDTO = new PostDTO("Title", "Content", "Author");
        PostResponseDTO postResponseDTO = new PostResponseDTO(postId, "Title", "Content", "Author", LocalDateTime.now(), true, PostStatus.PENDING, null);
        when(postService.createDraft(postDTO)).thenReturn(postResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/posts/createDraft")
                        .contentType("application/json")
                        .content("{\"title\":\"Title\",\"content\":\"Content\",\"author\":\"Author\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Now delete the draft
        doNothing().when(postService).deleteDraft(postId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/draft/{id}", postId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(postService, times(1)).deleteDraft(postId);
    }


}
