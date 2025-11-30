package userservice.service.impl;

import exception.UserNotFoundException;

import userservice.dto.UserMapper;
import userservice.dto.UserRequest;
import userservice.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import model.User;
import org.springframework.stereotype.Service;
import repository.UserRepository;
import userservice.service.KafkaUserEventProducer;
import userservice.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KafkaUserEventProducer eventProducer;

    @Override
    public UserResponse create(UserRequest request) {
        User user = UserMapper.toEntity(request);
        user = userRepository.save(user);
        eventProducer.sendUserCreated(user.getEmail());
        return UserMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(request.getName());
                    user.setEmail(request.getEmail());
                    user.setAge(request.getAge());
                    User updated = userRepository.save(user);
                    return UserMapper.toResponse(updated);
                })
                .orElseThrow(() -> new UserNotFoundException(id));    }

    @Override
    public void delete(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            userRepository.delete(user);
            eventProducer.sendUserDeleted(user.getEmail());
        });
    }
}