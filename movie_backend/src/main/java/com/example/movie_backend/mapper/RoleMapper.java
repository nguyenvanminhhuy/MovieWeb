package com.example.movie_backend.mapper;

import com.example.movie_backend.dto.request.RoleRequest;
import com.example.movie_backend.dto.response.RoleResponse;
import com.example.movie_backend.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
