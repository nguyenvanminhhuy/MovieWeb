package com.example.movie_backend.controller;

import com.example.movie_backend.dto.request.GenreRequest;
import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.GenreResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.service.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Genre Controller", description = "Quản lý thể loại phim (Admin)")
@PreAuthorize("hasRole('ADMIN')")
public class GenreController {
    GenreService genreService;

    @Operation(summary = "Tạo mới thể loại")
    @PostMapping
    ApiResponse<GenreResponse> create(@RequestBody GenreRequest request) {
        return ApiResponse.<GenreResponse>builder()
                .result(genreService.create(request))
                .build();
    }

    @Operation(summary = "Lấy tất cả thể loại")
    @GetMapping
    ApiResponse<PageResponse<GenreResponse>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<GenreResponse>>builder()
                .result(genreService.getAllPaginated(page, size))
                .build();
    }

    @Operation(summary = "Cập nhật thể loại")
    @PutMapping("/{id}")
    ApiResponse<GenreResponse> update(@PathVariable String id, @RequestBody GenreRequest request) {
        return ApiResponse.<GenreResponse>builder()
                .result(genreService.update(id, request))
                .build();
    }

    @Operation(summary = "Xóa thể loại")
    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id) {
        genreService.delete(id);
        return ApiResponse.<Void>builder().build();
    }
}
