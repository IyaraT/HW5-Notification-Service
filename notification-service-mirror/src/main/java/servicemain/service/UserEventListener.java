// src/main/java/notificationservice.service/UserEventListener.java
package servicemain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import servicemain.UserEvent;

@Service
@RequiredArgsConstructor
public class UserEventListener {

    private final EmailService emailService;

    @Value("${app.kafka.user-topic}")
    private String userTopic;

    @KafkaListener(
            topics = "${app.kafka.user-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "userEventKafkaListenerContainerFactory"
    )

    public void handleUserEvent(UserEvent event) {
        System.out.println("LISTENER ACTIVE! Received event: " + event);
        if ("CREATE".equalsIgnoreCase(event.getOperation())) {
            emailService.sendAccountCreated(event.getEmail());
        } else if ("DELETE".equalsIgnoreCase(event.getOperation())) {
            emailService.sendAccountDeleted(event.getEmail());
        }
    }
}