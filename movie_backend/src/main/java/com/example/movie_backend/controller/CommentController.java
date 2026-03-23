package com.example.movie_backend.controller;

import com.example.movie_backend.dto.request.CommentRequest;
import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.CommentResponse;
import com.example.movie_backend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Comment Controller", description = "Quản lý bình luận")
public class CommentController {
    CommentService commentService;

    @Operation(summary = "Viết bình luận mới")
    @PostMapping
    ApiResponse<CommentResponse> create(@RequestBody CommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.create(request))
                .build();
    }

    @Operation(summary = "Thích bình luận")
    @PostMapping("/{id}/like")
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
