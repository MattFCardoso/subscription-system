package com.globo.subscriptionapplication.service.impl;

import com.globo.subscriptionapplication.domain.model.User;
import com.globo.subscriptionapplication.domain.repository.UserRepository;
import com.globo.subscriptionapplication.domain.dto.request.CreateUserRequest;
import com.globo.subscriptionapplication.domain.dto.response.UserResponse;
import com.globo.subscriptionapplication.exception.UserServiceException;
import com.globo.subscriptionapplication.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserServiceException("User with email already exists: " + request.getEmail());
        }
        if (userRepository.existsByCpf(request.getCpf())) {
            throw new UserServiceException("User with CPF already exists: " + request.getCpf());
        }

        User user = User.builder()
                .name(request.getName())
                .cpf(request.getCpf())
                .email(request.getEmail())
                .createdAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);
        log.info("User created successfully with ID: {}", user.getUserId());

        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.debug("Finding user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserServiceException("User not found with email: " + email));
        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .userID(user.getUserId())
                .name(user.getName())
                .cpf(user.getCpf())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
