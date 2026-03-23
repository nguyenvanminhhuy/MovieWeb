package com.example.movie_backend.mapper;

import com.example.movie_backend.dto.request.PermissionRequest;
import com.example.movie_backend.dto.response.PermissionResponse;
import com.example.movie_backend.entity.Permissions;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permissions toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permissions permissions);
}
