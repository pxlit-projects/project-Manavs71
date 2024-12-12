package be.pxl.client;

import be.pxl.model.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/notification")
    public void sendMessage(@RequestBody NotificationRequest notificationRequest);
}
