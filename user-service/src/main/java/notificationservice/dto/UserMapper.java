package notificationservice.dto;

import model.User;


public class UserMapper {

    public static User toEntity(UserRequest req) {
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setAge(req.getAge());
        return user;
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .createdOn(user.getCreatedOn())
                .build();
    }
}