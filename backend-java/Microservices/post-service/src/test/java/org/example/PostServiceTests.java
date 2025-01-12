package org.example;

import org.example.DTO.CommentDTO;
import org.example.DTO.PostDTO;
import org.example.DTO.PostResponseDTO;
import org.example.clients.CommentClient;
import org.example.domain.Post;
import org.example.domain.PostStatus;
import org.example.repository.PostRepository;
import org.example.services.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentClient commentClient;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPostWithCommentsReturnsPostWithComments() {
        Long postId = 1L;
        Post post = new Post("Title", "Content", "Author");
        post.setId(postId);
        List<CommentDTO> comments = Collections.singletonList(new CommentDTO(postId, "Author", "Content", null));

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentClient.getCommentsForPost(postId)).thenReturn(comments);

        Post result = postService.getPostWithComments(postId);

        assertEquals(postId, result.getId());
        assertEquals(comments, result.getComments());
    }

    @Test
    void createPostReturnsPostResponseDTO() {
        PostDTO postDTO = new PostDTO("Title", "Content", "Author");
        Post post = new Post(postDTO.getTitle(), postDTO.getContent(), postDTO.getAuthor());
        post.setId(1L);

        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponseDTO result = postService.createPost(postDTO);

        assertEquals(post.getId(), result.getId());
        assertEquals(post.getTitle(), result.getTitle());
        assertEquals(post.getContent(), result.getContent());
        assertEquals(post.getAuthor(), result.getAuthor());
    }

    @Test
    void createDraftReturnsDraftPostResponseDTO() {
        PostDTO postDTO = new PostDTO("Title", "Content", "Author");
        Post draftPost = new Post(postDTO.getTitle(), postDTO.getContent(), postDTO.getAuthor());
        draftPost.setId(1L);
        draftPost.setDraft(true);

        when(postRepository.save(any(Post.class))).thenReturn(draftPost);

        PostResponseDTO result = postService.createDraft(postDTO);

        assertTrue(result.isDraft());
        assertEquals(draftPost.getId(), result.getId());
    }

    @Test
    void saveAsDraftUpdatesPostToDraft() {
        Long postId = 1L;
        Post post = new Post("Title", "Content", "Author");
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponseDTO result = postService.saveAsDraft(postId);

        assertTrue(result.isDraft());
    }

    @Test
    void publishUpdatesPostToNotDraft() {
        Long postId = 1L;
        Post post = new Post("Title", "Content", "Author");
        post.setId(postId);
        post.setDraft(true);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponseDTO result = postService.publish(postId);

        assertFalse(result.isDraft());
    }

    @Test
    void getDraftPostsReturnsListOfDraftPosts() {
        Post draftPost = new Post("Title", "Content", "Author");
        draftPost.setId(1L);
        draftPost.setDraft(true);

        when(postRepository.findByIsDraft(true)).thenReturn(Collections.singletonList(draftPost));

        List<PostResponseDTO> result = postService.getDraftPosts();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isDraft());
    }

    @Test
    void getPublishedPostsReturnsListOfPublishedPosts() {
        Post post = new Post("Title", "Content", "Author");
        post.setId(1L);
        post.setDraft(false);

        when(postRepository.findByIsDraft(false)).thenReturn(Collections.singletonList(post));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(commentClient.getCommentsForPost(post.getId())).thenReturn(Collections.emptyList());

        List<PostResponseDTO> result = postService.getPublishedPosts();

        assertEquals(1, result.size());
        assertFalse(result.get(0).isDraft());
    }

    @Test
    void editPostUpdatesPostDetails() {
        Long postId = 1L;
        PostDTO postDTO = new PostDTO("New Title", "New Content", "New Author");
        Post post = new Post("Title", "Content", "Author");
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponseDTO result = postService.editPost(postId, postDTO);

        assertEquals(postDTO.getTitle(), result.getTitle());
        assertEquals(postDTO.getContent(), result.getContent());
        assertEquals(postDTO.getAuthor(), result.getAuthor());
    }

    @Test
    void deleteDraftRemovesPost() {
        Long postId = 1L;
        Post post = new Post("Title", "Content", "Author");
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        doNothing().when(postRepository).delete(post);

        postService.deleteDraft(postId);

        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void updatePostStatusUpdatesStatusAndDraftFlag() {
        Long postId = 1L;
        Post post = new Post("Title", "Content", "Author");
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        postService.updatePostStatus(postId, PostStatus.APPROVED);

        assertEquals(PostStatus.APPROVED, post.getStatus());
        assertFalse(post.isDraft());
    }

    @Test
    void updateRejectionCommentUpdatesComment() {
        Long postId = 1L;
        Post post = new Post("Title", "Content", "Author");
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        postService.updateRejectionComment(postId, "Rejection Comment");

        assertEquals("Rejection Comment", post.getRejectionComment());
    }
}