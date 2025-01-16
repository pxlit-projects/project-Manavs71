package org.example;

import org.example.DTO.CommentDTO;
import org.example.DTO.PostDTO;
import org.example.DTO.PostResponseDTO;
import org.example.clients.CommentClient;
import org.example.domain.Post;
import org.example.domain.PostStatus;
import org.example.repository.PostRepository;
import org.example.services.PostService;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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
class PostServiceTests {

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
    private PostService postService;

    @MockBean
    private CommentClient commentClient;

    @BeforeEach
    void setup() {
        postRepository.deleteAll(); // Clean database before each test
    }

    @Test
    void getPostWithCommentsReturnsPostWithComments() {
        // Arrange
        Post post = new Post("Title", "Content", "Author");
        Post savedPost = postRepository.save(post); // Save and retrieve the saved entity
        Long postId = savedPost.getId(); // Get the auto-generated ID

        List<CommentDTO> comments = Collections.singletonList(
                new CommentDTO(postId, "Author", "Content", null)
        );
        when(commentClient.getCommentsForPost(postId)).thenReturn(comments);

        // Act
        Post result = postService.getPostWithComments(postId);

        // Assert
        assertNotNull(result, "Post should not be null");
        assertEquals(postId, result.getId(), "Post ID should match");
        assertEquals(comments, result.getComments(), "Comments should match");
    }

    @Test
    void createPostReturnsPostResponseDTO() {
        PostDTO postDTO = new PostDTO("Title", "Content", "Author");

        PostResponseDTO result = postService.createPost(postDTO);

        assertNotNull(result);
        assertEquals(postDTO.getTitle(), result.getTitle());
        assertEquals(postDTO.getContent(), result.getContent());
        assertEquals(postDTO.getAuthor(), result.getAuthor());
    }

    @Test
    void createDraftReturnsDraftPostResponseDTO() {
        PostDTO postDTO = new PostDTO("Title", "Content", "Author");

        PostResponseDTO result = postService.createDraft(postDTO);

        assertNotNull(result);
        assertTrue(result.isDraft());
        assertEquals(postDTO.getTitle(), result.getTitle());
    }

    @Test
    void saveAsDraftUpdatesPostToDraft() {
        Post post = new Post("Title", "Content", "Author");
        postRepository.save(post);

        PostResponseDTO result = postService.saveAsDraft(post.getId());

        assertNotNull(result);
        assertTrue(result.isDraft());
    }

    @Test
    void publishUpdatesPostToNotDraft() {
        Post post = new Post("Title", "Content", "Author");
        post.setDraft(true);
        postRepository.save(post);

        PostResponseDTO result = postService.publish(post.getId());

        assertNotNull(result);
        assertFalse(result.isDraft());
    }

    @Test
    void getDraftPostsReturnsListOfDraftPosts() {
        Post draftPost = new Post("Title", "Content", "Author");
        draftPost.setDraft(true);
        postRepository.save(draftPost);

        List<PostResponseDTO> result = postService.getDraftPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isDraft());
    }

    @Test
    void getPublishedPostsReturnsListOfPublishedPosts() {
        Post post = new Post("Title", "Content", "Author");
        post.setDraft(false);
        postRepository.save(post);

        when(commentClient.getCommentsForPost(post.getId())).thenReturn(Collections.emptyList());

        List<PostResponseDTO> result = postService.getPublishedPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isDraft());
    }

    @Test
    void editPostUpdatesPostDetails() {
        Post post = new Post("Title", "Content", "Author");
        postRepository.save(post);

        PostDTO postDTO = new PostDTO("New Title", "New Content", "New Author");
        PostResponseDTO result = postService.editPost(post.getId(), postDTO);

        assertNotNull(result);
        assertEquals(postDTO.getTitle(), result.getTitle());
        assertEquals(postDTO.getContent(), result.getContent());
    }

    @Test
    void deleteDraftRemovesPost() {
        Post post = new Post("Title", "Content", "Author");
        postRepository.save(post);

        postService.deleteDraft(post.getId());

        assertFalse(postRepository.findById(post.getId()).isPresent());
    }

    @Test
    void updatePostStatusUpdatesStatusAndDraftFlag() {
        Post post = new Post("Title", "Content", "Author");
        postRepository.save(post);

        postService.updatePostStatus(post.getId(), PostStatus.APPROVED);

        Post updatedPost = postRepository.findById(post.getId()).orElse(null);
        assertNotNull(updatedPost);
        assertEquals(PostStatus.APPROVED, updatedPost.getStatus());
        assertFalse(updatedPost.isDraft());
    }

    @Test
    void updateRejectionCommentUpdatesComment() {
        Post post = new Post("Title", "Content", "Author");
        postRepository.save(post);

        postService.updateRejectionComment(post.getId(), "Rejection Comment");

        Post updatedPost = postRepository.findById(post.getId()).orElse(null);
        assertNotNull(updatedPost);
        assertEquals("Rejection Comment", updatedPost.getRejectionComment());
    }
}
