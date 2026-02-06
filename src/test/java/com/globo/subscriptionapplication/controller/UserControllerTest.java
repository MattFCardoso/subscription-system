package com.globo.subscriptionapplication.controller;

    import com.globo.subscriptionapplication.domain.dto.request.CreateUserRequest;
    import com.globo.subscriptionapplication.domain.dto.response.UserResponse;
    import com.globo.subscriptionapplication.service.interfaces.UserService;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Nested;
    import org.junit.jupiter.api.Test;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.MockitoAnnotations;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;

    import java.time.LocalDateTime;
    import java.util.UUID;

    import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.mockito.Mockito.*;

    @DisplayName("UserController")
    class UserControllerTest {

        @Mock
        private UserService userService;

        @InjectMocks
        private UserController userController;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        @Nested
        @DisplayName("createUser")
        class CreateUser {

            @Test
            @DisplayName("Should create a user and return 201 status")
            void shouldCreateUserAndReturnCreatedStatus() {
                CreateUserRequest request = new CreateUserRequest("John", "Doe", "john.doe@example.com");
                UserResponse response = UserResponse.builder()
                        .userID(UUID.randomUUID())
                        .name("John Doe")
                        .cpf("12345678900")
                        .email("john.doe@example.com")
                        .createdAt(LocalDateTime.now())
                        .build();

                when(userService.createUser(request)).thenReturn(response);

                ResponseEntity<UserResponse> result = userController.createUser(request);

                assertEquals(HttpStatus.CREATED, result.getStatusCode());
                assertEquals(response, result.getBody());
                verify(userService, times(1)).createUser(request);
            }
        }

        @Nested
        @DisplayName("getUserByEmail")
        class GetUserByEmail {

            @Test
            @DisplayName("Should return user details for a valid email")
            void shouldReturnUserDetailsForValidEmail() {
                String email = "john.doe@example.com";
                UserResponse response = UserResponse.builder()
                        .userID(UUID.randomUUID())
                        .name("John Doe")
                        .cpf("12345678900")
                        .email("john.doe@example.com")
                        .createdAt(LocalDateTime.now())
                        .build();

                when(userService.getUserByEmail(email)).thenReturn(response);

                ResponseEntity<UserResponse> result = userController.getUserByEmail(email);

                assertEquals(HttpStatus.OK, result.getStatusCode());
                assertEquals(response, result.getBody());
                verify(userService, times(1)).getUserByEmail(email);
            }

            @Test
            @DisplayName("Should return 404 status when user is not found")
            void shouldReturnNotFoundWhenUserNotFound() {
                String email = "nonexistent@example.com";

                when(userService.getUserByEmail(email)).thenReturn(null);

                ResponseEntity<UserResponse> result = userController.getUserByEmail(email);

                assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
                verify(userService, times(1)).getUserByEmail(email);
            }
        }
    }