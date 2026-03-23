package com.example.movie_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String email;
    String fullName;
    String avatar;
    boolean enabled;
    Set<RoleResponse> roles;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
