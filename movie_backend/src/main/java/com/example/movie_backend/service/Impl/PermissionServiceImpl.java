package com.example.movie_backend.service.impl;

import com.example.movie_backend.dto.request.PermissionRequest;
import com.example.movie_backend.dto.response.PermissionResponse;
import com.example.movie_backend.entity.Permissions;
import com.example.movie_backend.mapper.PermissionMapper;
import com.example.movie_backend.repository.PermissionRepository;
import com.example.movie_backend.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionServiceImpl implements PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @Override
    public PermissionResponse create(PermissionRequest request) {
        Permissions permissions = permissionMapper.toPermission(request);
        permissions = permissionRepository.save(permissions);
        return permissionMapper.toPermissionResponse(permissions);
    }

    @Override
    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    @Override
    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}
