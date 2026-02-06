package com.globo.subscriptionapplication.service.interfaces;

import com.globo.subscriptionapplication.domain.dto.request.CreateUserRequest;
import com.globo.subscriptionapplication.domain.dto.response.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserByEmail(String email);
}
