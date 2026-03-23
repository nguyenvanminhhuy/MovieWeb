package com.example.movie_backend.controller;

import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.NotificationResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Notification Controller", description = "Quản lý thông báo (Notifications)")
public class NotificationController {
    NotificationService notificationService;

    @Operation(summary = "Lấy danh sách thông báo của tôi")
    @GetMapping
    ApiResponse<PageResponse<NotificationResponse>> getMyNotifications(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<NotificationResponse>>builder()
                .result(notificationService.getMyNotifications(page, size))
                .build();
    }

    @Operation(summary = "Đếm số lượng thông báo chưa đọc")
    @GetMapping("/unread-count")
    ApiResponse<Long> countUnread() {
        return ApiResponse.<Long>builder()
                .result(notificationService.countUnread())
                .build();
    }

    @Operation(summary = "Đánh dấu thông báo đã đọc")
    @PatchMapping("/{id}/read")
    ApiResponse<Void> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ApiResponse.<Void>builder().build();
    }

    @Operation(summary = "Đánh dấu tất cả thông báo là đã đọc")
    @PatchMapping("/read-all")
    ApiResponse<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponse.<Void>builder().build();
    }
}
