package notificationservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private String operation;
    private String email;
}
