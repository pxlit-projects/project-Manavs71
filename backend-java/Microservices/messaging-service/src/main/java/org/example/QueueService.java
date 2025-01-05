package org.example;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class QueueService {
    @RabbitListener(queues = "reject_queue")
    public void listen(String in) {
        System.out.println("Message read from myQueue : " + in);

    }
}
