package com.example.movie_backend.controller;

import com.example.movie_backend.dto.request.RoleRequest;
import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.RoleResponse;
import com.example.movie_backend.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Role Controller", description = "Quản lý vai trò (Roles) (Admin)")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {
    RoleService roleService;

    @Operation(summary = "Tạo mới vai trò")
    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody @Valid RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @Operation(summary = "Lấy danh sách tất cả vai trò")
    @GetMapping
    ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @Operation(summary = "Xóa vai trò")
    @DeleteMapping("/{role}")
    ApiResponse<Void> delete(@PathVariable String role) {
        roleService.delete(role);
        return ApiResponse.<Void>builder().build();
    }
}