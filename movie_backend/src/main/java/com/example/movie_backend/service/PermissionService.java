package com.example.movie_backend.service;

import com.example.movie_backend.dto.request.PermissionRequest;
import com.example.movie_backend.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    PermissionResponse create(PermissionRequest request);
    List<PermissionResponse> getAll();
    void delete(String permission);
}
