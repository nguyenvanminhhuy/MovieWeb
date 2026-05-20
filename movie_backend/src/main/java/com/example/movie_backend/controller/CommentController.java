package com.example.movie_backend.controller;

import com.example.movie_backend.dto.request.CommentRequest;
import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.CommentResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Comment Controller", description = "Quản lý bình luận")
public class CommentController {
    CommentService commentService;

    @Operation(summary = "Lấy tất cả bình luận (Quản lý)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<PageResponse<CommentResponse>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .result(commentService.getAll(page, size))
                .build();
    }

    @Operation(summary = "Viết bình luận mới")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    ApiResponse<CommentResponse> create(@RequestBody @Valid CommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.create(request))
                .build();
    }

    @Operation(summary = "Thích bình luận")
    @PostMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    ApiResponse<CommentResponse> like(@PathVariable String id) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.likeComment(id))
                .build();
    }

    @Operation(summary = "Xóa bình luận")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @commentServiceImpl.isOwner(#id)")
    ApiResponse<Void> delete(@PathVariable String id) {
        commentService.delete(id);
        return ApiResponse.<Void>builder().build();
    }
}