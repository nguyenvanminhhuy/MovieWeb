package com.example.movie_backend.mapper;

import com.example.movie_backend.dto.request.UserCreationRequest;
import com.example.movie_backend.dto.request.UserUpdateRequest;
import com.example.movie_backend.dto.response.UserResponse;
import com.example.movie_backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
