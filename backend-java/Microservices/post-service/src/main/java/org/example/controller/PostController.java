package org.example.controller;

import org.example.DTO.PostDTO;
import org.example.DTO.PostResponseDTO;
import org.example.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PostResponseDTO> createPost(@RequestBody PostDTO postDTO) {
        PostResponseDTO postResponse = postService.createPost(postDTO);
        return ResponseEntity.ok(postResponse);
    }


    @PostMapping("/draft/{postId}")
    public ResponseEntity<PostResponseDTO> savePostAsDraft(@PathVariable Long postId) {
        PostResponseDTO postResponse = postService.saveAsDraft(postId);
        return ResponseEntity.ok(postResponse);
    }

    // Endpoint voor het ophalen van alle concepten
    @GetMapping("/drafts")
    public ResponseEntity<List<PostResponseDTO>> getDraftPosts() {
        List<PostResponseDTO> drafts = postService.getDraftPosts();
        return ResponseEntity.ok(drafts);
    }

    // Endpoint voor het ophalen van alle gepubliceerde posts
    @GetMapping("/published")
    public ResponseEntity<List<PostResponseDTO>> getPublishedPosts() {
        List<PostResponseDTO> posts = postService.getPublishedPosts();
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/edit/{postId}")
    public ResponseEntity<PostResponseDTO> editPost(@PathVariable Long postId, @RequestBody PostDTO postDTO) {
        PostResponseDTO updatedPostResponse = postService.editPost(postId, postDTO);
        return ResponseEntity.ok(updatedPostResponse);
    }

    @DeleteMapping("/draft/{id}")
    public void deleteDraft(@PathVariable Long id) {
        postService.deleteDraft(id);
    }
}
