package com.example.movie_backend.controller;

import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.DashboardResponse;
import com.example.movie_backend.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Statistics Controller", description = "API thống kê cho Admin Dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {
    StatisticsService statisticsService;

    @Operation(summary = "Lấy dữ liệu thống kê tổng quan")
    @GetMapping("/dashboard")
    ApiResponse<DashboardResponse> getDashboardStats() {
        return ApiResponse.<DashboardResponse>builder()
                .result(statisticsService.getDashboardStats())
                .build();
    }
}
