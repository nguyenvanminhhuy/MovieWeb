package com.example.movie_backend.controller;

import com.example.movie_backend.dto.request.MovieRequest;
import com.example.movie_backend.dto.response.MovieResponse;
import com.example.movie_backend.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Movie Controller", description = "Quản lý danh sách phim Anime (Admin)")
@PreAuthorize("hasRole('ADMIN')")
public class MovieController {
        MovieService movieService;

        @Operation(summary = "Lấy danh sách phim", description = "Trả về danh sách tất cả các bộ phim hiện có")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "1000", description = "Thành công"),
            @ApiResponse(responseCode = "9999", description = "Lỗi không xác định")
    })
    @GetMapping
    public com.example.movie_backend.dto.response.ApiResponse<com.example.movie_backend.dto.response.PageResponse<MovieResponse>> getAllMovies(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        return com.example.movie_backend.dto.response.ApiResponse.<com.example.movie_backend.dto.response.PageResponse<MovieResponse>>builder()
                .result(movieService.getAll(page, size))
                .build();
    }

        @Operation(summary = "Lấy thông tin phim theo ID")
        @GetMapping("/{id}")
        public com.example.movie_backend.dto.response.ApiResponse<MovieResponse> getById(@PathVariable String id) {
                return com.example.movie_backend.dto.response.ApiResponse.<MovieResponse>builder()
                                .result(movieService.getById(id))
                                .build();
        }

        @Operation(summary = "Tạo mới phim", description = "Thêm một bộ phim anime mới vào hệ thống")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "1000", description = "Tạo phim thành công"),
                        @ApiResponse(responseCode = "1001", description = "Dữ liệu không hợp lệ")
        })
        @PostMapping
        public com.example.movie_backend.dto.response.ApiResponse<MovieResponse> createMovie(
                        @RequestBody @Valid MovieRequest request) {
                return com.example.movie_backend.dto.response.ApiResponse.<MovieResponse>builder()
                                .result(movieService.create(request))
                                .build();
        }

        @Operation(summary = "Cập nhật phim")
        @PutMapping("/{id}")
        public com.example.movie_backend.dto.response.ApiResponse<MovieResponse> updateMovie(@PathVariable String id,
                        @RequestBody @Valid MovieRequest request) {
                return com.example.movie_backend.dto.response.ApiResponse.<MovieResponse>builder()
                                .result(movieService.update(id, request))
                                .build();
        }

        @Operation(summary = "Xóa phim")
        @DeleteMapping("/{id}")
        public com.example.movie_backend.dto.response.ApiResponse<Void> deleteMovie(@PathVariable String id) {
                movieService.delete(id);
                return com.example.movie_backend.dto.response.ApiResponse.<Void>builder().build();
        }
}
