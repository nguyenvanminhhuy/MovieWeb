package com.example.movie_backend.controller;

import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.entity.AuditLog;
import com.example.movie_backend.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/audit-logs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Audit Log", description = "API quản lý lịch sử hoạt động hệ thống")
public class AuditLogController {
    AuditLogService auditLogService;

    @Operation(summary = "Xem danh sách lịch sử hoạt động", description = "Chỉ dành cho Admin")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<PageResponse<AuditLog>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<AuditLog>>builder()
                .result(auditLogService.getAll(page, size))
                .build();
    }
}
