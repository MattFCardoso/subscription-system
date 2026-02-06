package com.globo.subscriptionapplication.service.impl;

import com.globo.subscriptionapplication.dto.request.CreateUserRequest;
import com.globo.subscriptionapplication.dto.response.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserByEmail(String email);
}
