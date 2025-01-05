package org.example;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

    //dummy queue to keep the service alive
    @RabbitListener(queues = "dummy_queue")
    public void listen(String in) {
        System.out.println("Message read from dummy_queue : " + in);

    }
}
