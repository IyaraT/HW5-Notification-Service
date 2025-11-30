package userservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEvent {
    private String operation;
    private String email;
}