package org.example.controller;

import org.example.DTO.PostDTO;
import org.example.DTO.PostResponseDTO;
import org.example.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createPost(@RequestBody PostDTO postDTO) {
        if (postDTO.getTitle() == null || postDTO.getTitle().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "Title cannot be empty"));
        }
        try {
            PostResponseDTO postResponse = postService.createPost(postDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PostMapping(value = "/createDraft", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PostResponseDTO> createDraft(@RequestBody PostDTO postDTO) {
        try{
            PostResponseDTO draftRespone = postService.createDraft(postDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(draftRespone);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/rejection-comment")
    public void updateRejectionComment(@RequestParam Long postId, @RequestParam String comment) {
        postService.updateRejectionComment(postId, comment);
    }


    @PostMapping("/draft/{postId}")
    public ResponseEntity<PostResponseDTO> savePostAsDraft(@PathVariable Long postId) {
        PostResponseDTO postResponse = postService.saveAsDraft(postId);
        return ResponseEntity.ok(postResponse);
    }

    @PostMapping("/publish/{postId}")
    public ResponseEntity<PostResponseDTO> publishPost(@PathVariable Long postId) {

        try {
            PostResponseDTO postResponse = postService.publish(postId);
            return ResponseEntity.ok(postResponse);
        }
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }


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

        try{
            List<PostResponseDTO> posts = postService.getPublishedPosts();
            return ResponseEntity.ok(posts);
        }
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("/edit/{postId}")
    public ResponseEntity<PostResponseDTO> editPost(@PathVariable Long postId, @RequestBody PostDTO postDTO) {
        PostResponseDTO updatedPostResponse = postService.editPost(postId, postDTO);
        return ResponseEntity.ok(updatedPostResponse);
    }

    @DeleteMapping("/draft/{id}")
    public ResponseEntity<Void> deleteDraft(@PathVariable Long id) {
        try {
            postService.deleteDraft(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();

        }
    }

}
