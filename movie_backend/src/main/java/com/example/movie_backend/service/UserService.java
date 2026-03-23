package com.example.movie_backend.service;

import com.example.movie_backend.dto.request.UserCreationRequest;
import com.example.movie_backend.dto.request.UserUpdateRequest;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createRequest(UserCreationRequest request);

    List<UserResponse> getUsers();

    PageResponse<UserResponse> getUsersPaginated(int page, int size);

    UserResponse getUser(String id);

    UserResponse getMyInfo();

    UserResponse updateUser(String id, UserUpdateRequest request);

    void deleteUser(String id);

    void setEnabled(String id, boolean enabled);
}
