package userservice.service;

import userservice.dto.UserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaUserEventProducer {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${app.kafka.user-topic}")
    private String userTopic;

    public void sendUserCreated(String email) {
        UserEvent event = new UserEvent("CREATE", email);
        kafkaTemplate.send(userTopic, email, event);
    }

    public void sendUserDeleted(String email) {
        UserEvent event = new UserEvent("DELETE", email);
        kafkaTemplate.send(userTopic, email, event);
    }
}