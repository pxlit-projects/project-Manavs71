package org.example;

import org.example.DTO.PostDTO;
import org.example.DTO.PostResponseDTO;
import org.example.controller.PostController;
import org.example.domain.PostStatus;
import org.example.services.PostService;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostControllerTests {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPostReturnsCreatedStatus() {
        PostDTO postDTO = new PostDTO("Title", "Content", "Author");
        PostResponseDTO postResponseDTO = new PostResponseDTO(10L, "Title", "Content", "Author", LocalDateTime.now(), false, PostStatus.PENDING,"dummy rejection comment");

        when(postService.createPost(postDTO)).thenReturn(postResponseDTO);

        ResponseEntity<PostResponseDTO> response = postController.createPost(postDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(postResponseDTO, response.getBody());
    }

    @Test
    void createDraftReturnsCreatedStatus() {
        PostDTO postDTO = new PostDTO("Title", "Content", "Author");
        PostResponseDTO draftResponseDTO = new PostResponseDTO(10L, "Title", "Content", "Author", LocalDateTime.now(), true, PostStatus.PENDING,"dummy rejection comment");

        when(postService.createDraft(postDTO)).thenReturn(draftResponseDTO);

        ResponseEntity<PostResponseDTO> response = postController.createDraft(postDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(draftResponseDTO, response.getBody());
    }

    @Test
    void updateRejectionCommentUpdatesComment() {
        Long postId = 1L;
        String comment = "Rejection Comment";

        doNothing().when(postService).updateRejectionComment(postId, comment);

        postController.updateRejectionComment(postId, comment);

        verify(postService, times(1)).updateRejectionComment(postId, comment);
    }

    @Test
    void savePostAsDraftReturnsOkStatus() {
        Long postId = 1L;
        PostResponseDTO postResponseDTO = new PostResponseDTO(10L, "Title", "Content", "Author", LocalDateTime.now(), false, PostStatus.PENDING,"dummy rejection comment");

        when(postService.saveAsDraft(postId)).thenReturn(postResponseDTO);

        ResponseEntity<PostResponseDTO> response = postController.savePostAsDraft(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postResponseDTO, response.getBody());
    }

    @Test
    void publishPostReturnsOkStatus() {
        Long postId = 1L;
        PostResponseDTO postResponseDTO = new PostResponseDTO(10L, "Title", "Content", "Author", LocalDateTime.now(), false, PostStatus.PENDING,"dummy rejection comment");

        when(postService.publish(postId)).thenReturn(postResponseDTO);

        ResponseEntity<PostResponseDTO> response = postController.publishPost(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postResponseDTO, response.getBody());
    }

    @Test
    void getDraftPostsReturnsListOfDrafts() {
        PostResponseDTO draftResponseDTO = new PostResponseDTO(10L, "Title", "Content", "Author", LocalDateTime.now(), true, PostStatus.PENDING,"dummy rejection comment");

        when(postService.getDraftPosts()).thenReturn(Collections.singletonList(draftResponseDTO));

        ResponseEntity<List<PostResponseDTO>> response = postController.getDraftPosts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).isDraft());
    }

    @Test
    void getPublishedPostsReturnsListOfPublishedPosts() {
        PostResponseDTO postResponseDTO = new PostResponseDTO(10L, "Title", "Content", "Author", LocalDateTime.now(), false, PostStatus.PENDING,"dummy rejection comment");

        when(postService.getPublishedPosts()).thenReturn(Collections.singletonList(postResponseDTO));

        ResponseEntity<List<PostResponseDTO>> response = postController.getPublishedPosts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertFalse(response.getBody().get(0).isDraft());
    }

    @Test
    void editPostReturnsUpdatedPost() {
        Long postId = 1L;
        PostDTO postDTO = new PostDTO("New Title", "New Content", "New Author");
        PostResponseDTO updatedPostResponseDTO = new PostResponseDTO(10L, "Title", "Content", "Author", LocalDateTime.now(), true, PostStatus.PENDING,"dummy rejection comment");

        when(postService.editPost(postId, postDTO)).thenReturn(updatedPostResponseDTO);

        ResponseEntity<PostResponseDTO> response = postController.editPost(postId, postDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedPostResponseDTO, response.getBody());
    }

    @Test
    void deleteDraftRemovesDraft() {
        Long postId = 1L;

        doNothing().when(postService).deleteDraft(postId);

        postController.deleteDraft(postId);

        verify(postService, times(1)).deleteDraft(postId);
    }
}