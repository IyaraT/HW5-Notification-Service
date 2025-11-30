package notificationservice.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import notificationservice.service.EmailService;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendEmail(@RequestBody SendNotificationRequest request) {
        if ("CREATE".equalsIgnoreCase(request.getOperation())) {
            emailService.sendAccountCreated(request.getEmail());
        } else if ("DELETE".equalsIgnoreCase(request.getOperation())) {
            emailService.sendAccountDeleted(request.getEmail());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }

    @Data
    public static class SendNotificationRequest {
        private String operation;
        private String email;
    }
}