package com.example.movie_backend.controller;

import com.example.movie_backend.dto.request.WatchHistoryRequest;
import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.dto.response.WatchHistoryResponse;
import com.example.movie_backend.service.WatchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Watch History Controller", description = "Quản lý lịch sử xem phim của người dùng")
public class WatchHistoryController {
    WatchHistoryService watchHistoryService;

    @Operation(summary = "Lưu tiến trình xem phim")
    @PostMapping
    ApiResponse<Void> saveProgress(@RequestBody WatchHistoryRequest request) {
        watchHistoryService.saveProgress(request);
        return ApiResponse.<Void>builder().build();
    }

    @Operation(summary = "Lấy lịch sử xem phim của tôi")
    @GetMapping
    ApiResponse<PageResponse<WatchHistoryResponse>> getMyHistory(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<WatchHistoryResponse>>builder()
                .result(watchHistoryService.getMyHistory(page, size))
                .build();
    }
}
