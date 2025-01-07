package org.example.Controller;

import org.example.domain.Comment;
import org.example.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comments")
@CrossOrigin(origins = "http://localhost:4200")

public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Create a new comment
    @PostMapping("/create")
    public ResponseEntity<Comment> createComment(
            @RequestParam Long postId,
            @RequestParam String username,
            @RequestParam String content) {
        Comment newComment = commentService.createComment(postId, username, content);
        return new ResponseEntity<>(newComment, HttpStatus.CREATED);
    }

    // Get all comments for a post
    @GetMapping("/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    // Update a comment
    @PutMapping("/update/{id}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long id,
            @RequestParam String content) {
        Optional<Comment> updatedComment = commentService.updateComment(id, content);
        return updatedComment.map(comment -> new ResponseEntity<>(comment, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
