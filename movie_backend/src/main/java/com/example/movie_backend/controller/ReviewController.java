package com.example.movie_backend.controller;

import com.example.movie_backend.dto.request.ReviewRequest;
import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.dto.response.ReviewResponse;
import com.example.movie_backend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Review Controller", description = "Quản lý đánh giá phim")
public class ReviewController {
    ReviewService reviewService;

    @Operation(summary = "Lấy tất cả đánh giá (Quản lý)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<PageResponse<ReviewResponse>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<ReviewResponse>>builder()
                .result(reviewService.getAll(page, size))
                .build();
    }

    @Operation(summary = "Viết đánh giá mới")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    ApiResponse<ReviewResponse> create(@RequestBody @Valid ReviewRequest request) {
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.create(request))
                .build();
    }

    @Operation(summary = "Xóa đánh giá")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @reviewServiceImpl.isOwner(#id)")
    ApiResponse<Void> delete(@PathVariable String id) {
        reviewService.delete(id);
        return ApiResponse.<Void>builder().build();
    }
}
