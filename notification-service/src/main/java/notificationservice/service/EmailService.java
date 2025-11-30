package notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    public void sendAccountCreated(String to) {
        String subject = "Аккаунт создан";
        String text = "Аккаунт успешно создан";
        send(to, subject, text);
    }

    public void sendAccountDeleted(String to) {
        String subject = "Аккаунт удален";
        String text = "Аккаунт был удален";
        send(to, subject, text);
    }

    public void send(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }
}