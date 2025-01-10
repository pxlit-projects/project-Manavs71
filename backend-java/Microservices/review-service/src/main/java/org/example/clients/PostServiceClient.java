package org.example.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "post-service", url = "http://localhost:8030/postService")

public interface PostServiceClient {
    @PutMapping("/posts/rejection-comment")
    void updateRejectionComment(@RequestParam("postId") Long postId, @RequestParam("comment") String comment);
}
