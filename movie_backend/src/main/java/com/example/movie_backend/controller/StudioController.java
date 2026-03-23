package com.example.movie_backend.controller;

import com.example.movie_backend.dto.request.StudioRequest;
import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.dto.response.StudioResponse;
import com.example.movie_backend.service.StudioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/studios")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Studio Controller", description = "Quản lý studio sản xuất (Admin)")
@PreAuthorize("hasRole('ADMIN')")
public class StudioController {
    StudioService studioService;

    @Operation(summary = "Tạo mới studio")
    @PostMapping
    ApiResponse<StudioResponse> create(@RequestBody StudioRequest request) {
        return ApiResponse.<StudioResponse>builder()
                .result(studioService.create(request))
                .build();
    }

    @Operation(summary = "Lấy tất cả studio")
    @GetMapping
    ApiResponse<PageResponse<StudioResponse>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<StudioResponse>>builder()
                .result(studioService.getAllPaginated(page, size))
                .build();
    }

    @Operation(summary = "Cập nhật studio")
    @PutMapping("/{id}")
    ApiResponse<StudioResponse> update(@PathVariable String id, @RequestBody StudioRequest request) {
        return ApiResponse.<StudioResponse>builder()
                .result(studioService.update(id, request))
                .build();
    }

    @Operation(summary = "Xóa studio")
    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id) {
        studioService.delete(id);
        return ApiResponse.<Void>builder().build();
    }
}
