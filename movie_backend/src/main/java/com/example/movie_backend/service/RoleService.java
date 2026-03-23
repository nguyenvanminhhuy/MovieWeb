package com.example.movie_backend.service;

import com.example.movie_backend.dto.request.RoleRequest;
import com.example.movie_backend.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse create(RoleRequest request);

    List<RoleResponse> getAll();

    void delete(String role);
}
