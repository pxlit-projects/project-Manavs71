package org.example.services;


import org.example.DTO.CommentDTO;
import org.example.DTO.PostDTO;
import org.example.DTO.PostResponseDTO;
import org.example.clients.CommentClient;
import org.example.domain.Post;
import org.example.domain.PostStatus;
import org.example.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentClient commentClient;

    public Post getPostWithComments(Long postId) {
        // Fetch the post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Fetch comments via Feign client
        List<CommentDTO> comments = commentClient.getCommentsForPost(postId);

        post.setComments(comments);

        return post;
    }

    public PostResponseDTO createPost(PostDTO postDTO) {
        try {
            Post post = new Post(postDTO.getTitle(), postDTO.getContent(), postDTO.getAuthor());
            post.setDraft(false);
            post = postRepository.save(post);
            return new PostResponseDTO(post.getId(), post.getTitle(), post.getContent(), post.getAuthor(), post.getCreatedDate(), post.isDraft(), post.getStatus());
        } catch (Exception e) {
            throw new RuntimeException("Error while creating the post: " + e.getMessage(), e);
        }
    }

    public PostResponseDTO createDraft(PostDTO postDTO) {
        try {
            Post draftPost = new Post(postDTO.getTitle(), postDTO.getContent(), postDTO.getAuthor());
            draftPost.setDraft(true);
            draftPost = postRepository.save(draftPost);
            return new PostResponseDTO(draftPost.getId(), draftPost.getTitle(), draftPost.getContent(), draftPost.getAuthor(), draftPost.getCreatedDate(), draftPost.isDraft(), draftPost.getStatus());
        } catch (Exception e) {
            throw new RuntimeException("Error while creating the draft: " + e.getMessage(), e);
        }
    }




    public PostResponseDTO saveAsDraft(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.setDraft(true);
        post = postRepository.save(post);

        return new PostResponseDTO(post.getId(), post.getTitle(), post.getContent(), post.getAuthor(), post.getCreatedDate(), post.isDraft(), post.getStatus());
    }

    public PostResponseDTO publish(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.setDraft(false);
        post = postRepository.save(post);
        return new PostResponseDTO(post.getId(), post.getTitle(), post.getContent(), post.getAuthor(), post.getCreatedDate(), post.isDraft(), post.getStatus());
    }

    public List<PostResponseDTO> getDraftPosts() {
        List<Post> drafts = postRepository.findByIsDraft(true);
        return drafts.stream()
                .map(post -> new PostResponseDTO(post.getId(), post.getTitle(), post.getContent(), post.getAuthor(), post.getCreatedDate(), post.isDraft(), post.getStatus()))
                .collect(Collectors.toList());
    }

   /* public List<PostResponseDTO> getPublishedPosts() {
        List<Post> posts = postRepository.findByIsDraft(false);
        return posts.stream()
                .map(post -> new PostResponseDTO(post.getId(), post.getTitle(), post.getContent(), post.getAuthor(), post.getCreatedDate(), post.isDraft(), post.getStatus()))
                .collect(Collectors.toList());
    }*/

    public List<PostResponseDTO> getPublishedPosts() {
        // Fetch the list of published posts (those that are not drafts)
        List<Post> posts = postRepository.findByIsDraft(false);

        // For each post, fetch the comments using the getPostWithComments method
        return posts.stream()
                .map(post -> {
                    // Fetch post with comments
                    Post postWithComments = getPostWithComments(post.getId());

                    // Map the post object with comments to the PostResponseDTO
                    PostResponseDTO dto =  new PostResponseDTO(
                            postWithComments.getId(),
                            postWithComments.getTitle(),
                            postWithComments.getContent(),
                            postWithComments.getAuthor(),
                            postWithComments.getCreatedDate(),
                            postWithComments.isDraft(),
                            postWithComments.getStatus()
                    );


                    dto.setComments(postWithComments.getComments());
                    return dto;
                })
                .collect(Collectors.toList());
    }




    public PostResponseDTO editPost(Long postId, PostDTO postDTO) {
        // Check if the post exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id " + postId));

        // Update the post's properties with the new values from the DTO
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setAuthor(postDTO.getAuthor());

        // Save the updated post back to the repository
        post = postRepository.save(post);

        // Return the updated PostResponseDTO
        return new PostResponseDTO(post.getId(), post.getTitle(), post.getContent(), post.getAuthor(), post.getCreatedDate(), post.isDraft(), post.getStatus());
    }

    public void deleteDraft(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        postRepository.delete(post);
    }

    public void updatePostStatus(Long postId, PostStatus status) {
        System.out.println("Updating post ID: " + postId + " to status: " + status);
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        post.setStatus(status);

        postRepository.save(post);
    }
}
