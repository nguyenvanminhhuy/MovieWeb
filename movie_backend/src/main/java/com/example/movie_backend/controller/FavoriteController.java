package com.example.movie_backend.controller;

import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.MovieResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Favorite Controller", description = "Quản lý phim yêu thích của người dùng")
public class FavoriteController {
    FavoriteService favoriteService;

    @Operation(summary = "Thêm phim vào danh sách yêu thích")
    @PostMapping("/{movieId}")
    ApiResponse<Void> addFavorite(@PathVariable String movieId) {
        favoriteService.addFavorite(movieId);
        return ApiResponse.<Void>builder().build();
    }

    @Operation(summary = "Xóa phim khỏi danh sách yêu thích")
    @DeleteMapping("/{movieId}")
    ApiResponse<Void> removeFavorite(@PathVariable String movieId) {
        favoriteService.removeFavorite(movieId);
        return ApiResponse.<Void>builder().build();
    }

    @Operation(summary = "Lấy danh sách phim yêu thích của tôi")
    @GetMapping
    ApiResponse<PageResponse<MovieResponse>> getMyFavorites(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .result(favoriteService.getMyFavorites(page, size))
                .build();
    }
}
