package com.example.movie_backend.controller;

import com.example.movie_backend.dto.request.FranchiseRequest;
import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.FranchiseResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.service.FranchiseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/franchises")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Franchise Controller", description = "Quản lý chuỗi phim (Franchises)")
public class FranchiseController {
    FranchiseService franchiseService;

    @Operation(summary = "Tạo mới Franchise")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<FranchiseResponse> create(@RequestBody FranchiseRequest request) {
        return ApiResponse.<FranchiseResponse>builder()
                .result(franchiseService.create(request))
                .build();
    }

    @Operation(summary = "Lấy danh sách Franchise")
    @GetMapping
    ApiResponse<PageResponse<FranchiseResponse>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<FranchiseResponse>>builder()
                .result(franchiseService.getAll(page, size))
                .build();
    }

    @Operation(summary = "Lấy Franchise theo ID")
    @GetMapping("/{id}")
    ApiResponse<FranchiseResponse> getById(@PathVariable String id) {
        return ApiResponse.<FranchiseResponse>builder()
                .result(franchiseService.getById(id))
                .build();
    }

    @Operation(summary = "Cập nhật Franchise")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<FranchiseResponse> update(@PathVariable String id, @RequestBody FranchiseRequest request) {
        return ApiResponse.<FranchiseResponse>builder()
                .result(franchiseService.update(id, request))
                .build();
    }

    @Operation(summary = "Xóa Franchise")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Void> delete(@PathVariable String id) {
        franchiseService.delete(id);
        return ApiResponse.<Void>builder().build();
    }
}
