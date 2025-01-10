package org.example.clients;

import org.example.DTO.CommentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "comment-service", url = "http://localhost:8030/commentService/comments") // Adjust URL to your CommentService URL

public interface CommentClient {
    @GetMapping("/{postId}")
    List<CommentDTO> getCommentsForPost(@PathVariable("postId") Long postId);
}
