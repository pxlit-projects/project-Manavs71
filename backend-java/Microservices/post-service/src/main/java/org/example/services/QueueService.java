package org.example.services;

import org.example.domain.PostStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueueService {
    @Autowired
    private PostService postService;

    @RabbitListener(queues = "approve_queue")
    public void listenToApproveQueue(String message) {
        //for debug purpose
        System.out.println("Received approval message: " + message);

        try {
            Long postId = extractPostIdFromMessage(message);
            postService.updatePostStatus(postId, PostStatus.APPROVED);
            System.out.println("Post with ID " + postId + " marked as approved.");
        } catch (Exception e) {
            System.err.println("Error processing approval message: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "reject_queue")
    public void listenToRejectQueue(String message) {
        // for debug purpose
        System.out.println("Received rejection message: " + message);

        try{
            Long postId = extractPostIdFromMessage(message);
            postService.updatePostStatus(postId, PostStatus.REJECTED);
            System.out.println("Post with ID " + postId + " marked as rejected.");
        }
       catch (Exception e){
           System.err.println("Error processing approval message: " + e.getMessage());
       }
    }

    private Long extractPostIdFromMessage(String message) {
        String[] parts = message.split(" ");
        return Long.valueOf(parts[2]);
    }
}
