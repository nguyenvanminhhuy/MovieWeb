package com.example.movie_backend.controller;

import com.example.movie_backend.dto.request.UserCreationRequest;
import com.example.movie_backend.dto.request.UserUpdateRequest;
import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.dto.response.UserResponse;
import com.example.movie_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "User Controller", description = "Quản lý người dùng (Users)")
public class UserController {
    UserService userService;

    @Operation(summary = "Lấy thông tin cá nhân của người dùng đang đăng nhập")
    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @Operation(summary = "Tạo mới người dùng")
    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createRequest(request))
                .build();
    }

    @Operation(summary = "Lấy danh sách tất cả người dùng")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<PageResponse<UserResponse>> getUsers(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .result(userService.getUsersPaginated(page, size))
                .build();
    }

    @Operation(summary = "Lấy thông tin người dùng theo ID")
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @Operation(summary = "Cập nhật thông tin người dùng")
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @Operation(summary = "Xóa người dùng")
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder().result("User has been deleted").build();
    }

    @Operation(summary = "Cập nhật thông tin cá nhân")
    @PutMapping("/update-profile")
    ApiResponse<UserResponse> updateProfile(@RequestBody UserUpdateRequest request) {
        String currentUserId = userService.getMyInfo().getId();
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(currentUserId, request))
                .build();
    }

    @Operation(summary = "Khóa/Mở khóa tài khoản người dùng")
    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Void> setUserStatus(@PathVariable String userId, @RequestParam boolean enabled) {
        userService.setEnabled(userId, enabled);
        return ApiResponse.<Void>builder().build();
    }
}
