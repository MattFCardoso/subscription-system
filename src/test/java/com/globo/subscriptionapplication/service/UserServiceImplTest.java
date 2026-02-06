package com.globo.subscriptionapplication.service;

import com.globo.subscriptionapplication.domain.model.User;
import com.globo.subscriptionapplication.domain.repository.UserRepository;
import com.globo.subscriptionapplication.dto.request.CreateUserRequest;
import com.globo.subscriptionapplication.dto.response.UserResponse;
import com.globo.subscriptionapplication.exception.UserServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUserSuccessfully() {
        CreateUserRequest request = new CreateUserRequest("John Doe", "12345678901", "john.doe@example.com");
        User user = User.builder()
                .userId(UUID.randomUUID())
                .name(request.getName())
                .cpf(request.getCpf())
                .email(request.getEmail())
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByCpf(request.getCpf())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals(user.getUserId(), response.getUserID());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getCpf(), response.getCpf());
        assertEquals(user.getEmail(), response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserThrowsExceptionWhenEmailExists() {
        CreateUserRequest request = new CreateUserRequest("John Doe", "12345678901", "john.doe@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        UserServiceException exception = assertThrows(UserServiceException.class, () -> userService.createUser(request));
        assertEquals("User with email already exists: " + request.getEmail(), exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUserThrowsExceptionWhenCpfExists() {
        CreateUserRequest request = new CreateUserRequest("John Doe", "12345678901", "john.doe@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByCpf(request.getCpf())).thenReturn(true);

        UserServiceException exception = assertThrows(UserServiceException.class, () -> userService.createUser(request));
        assertEquals("User with CPF already exists: " + request.getCpf(), exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserByEmailSuccessfully() {
        String email = "john.doe@example.com";
        User user = User.builder()
                .userId(UUID.randomUUID())
                .name("John Doe")
                .cpf("12345678901")
                .email(email)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserByEmail(email);

        assertNotNull(response);
        assertEquals(user.getUserId(), response.getUserID());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getCpf(), response.getCpf());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void getUserByEmailThrowsExceptionWhenNotFound() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserServiceException exception = assertThrows(UserServiceException.class, () -> userService.getUserByEmail(email));
        assertEquals("User not found with email: " + email, exception.getMessage());
    }
}