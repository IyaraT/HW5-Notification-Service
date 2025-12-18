package servicemain.service;

import servicemain.dto.UserRequest;
import servicemain.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(UserRequest request);
    List<UserResponse> getAll();
    UserResponse getById(Long id);
    UserResponse update(Long id, UserRequest request);
    void delete(Long id);
}