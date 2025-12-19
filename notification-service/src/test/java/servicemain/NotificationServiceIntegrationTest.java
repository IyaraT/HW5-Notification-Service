package servicemain;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, topics = {"user-events"})
class NotificationServiceIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "password"))
            .withPerMethodLifecycle(false);

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> System.getProperty("spring.embedded.kafka.brokers"));

        registry.add("spring.kafka.producer.key-serializer", () -> "org.apache.kafka.common.serialization.StringSerializer");
        registry.add("spring.kafka.producer.value-serializer", () -> "org.springframework.kafka.support.serializer.JsonSerializer");

        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> greenMail.getSmtp().getPort());
        registry.add("spring.mail.username", () -> "user");
        registry.add("spring.mail.password", () -> "password");
    }

    @Test
    void shouldSendEmailWhenUserCreatedEventReceived() {
        String email = "test@test.com";
        UserEvent event = new UserEvent("CREATE", email);

        kafkaTemplate.send("user-events", email, event);

        await().atMost(10, TimeUnit.SECONDS).until(() -> greenMail.getReceivedMessages().length > 0);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage msg = receivedMessages[0];
        try {
            assertEquals(email, msg.getAllRecipients()[0].toString());
            assertEquals("Аккаунт создан", msg.getSubject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldSendEmailViaApi() {
        String email = "test@test.com";
        UserEvent request = new UserEvent("DELETE", email);

        ResponseEntity<Void> response = restTemplate.postForEntity("/api/notifications/send", request, Void.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());

        await().atMost(5, TimeUnit.SECONDS).until(() -> greenMail.getReceivedMessages().length > 0);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

        MimeMessage msg = receivedMessages[receivedMessages.length - 1];
        try {
            assertEquals(email, msg.getAllRecipients()[0].toString());
            assertEquals("Аккаунт удален", msg.getSubject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}