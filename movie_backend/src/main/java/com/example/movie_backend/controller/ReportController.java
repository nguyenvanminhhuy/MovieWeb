package com.example.movie_backend.controller;

import com.example.movie_backend.dto.request.ReportRequest;
import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.dto.response.ReportResponse;
import com.example.movie_backend.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Report Controller", description = "Quản lý báo cáo (Reports)")
public class ReportController {
    ReportService reportService;

    @Operation(summary = "Gửi báo cáo lỗi/vi phạm")
    @PostMapping
    ApiResponse<ReportResponse> createReport(@RequestBody ReportRequest request) {
        return ApiResponse.<ReportResponse>builder()
                .result(reportService.create(request))
                .build();
    }

    @Operation(summary = "Lấy danh sách tất cả báo cáo")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<PageResponse<ReportResponse>> getAllReports(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ReportResponse>>builder()
                .result(reportService.getAll(page, size))
                .build();
    }

    @Operation(summary = "Đánh dấu báo cáo đã xử lý")
    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Void> resolveReport(@PathVariable String id) {
        reportService.resolve(id);
        return ApiResponse.<Void>builder().build();
    }
}
