package com.example.movie_backend.controller;

import com.example.movie_backend.dto.request.EpisodeRequest;
import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.EpisodeResponse;
import com.example.movie_backend.service.EpisodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/episodes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Episode Controller", description = "Quản lý tập phim (Admin)")
@PreAuthorize("hasRole('ADMIN')")
public class EpisodeController {
    EpisodeService episodeService;

    @Operation(summary = "Tạo mới tập phim")
    @PostMapping
    ApiResponse<EpisodeResponse> create(@RequestBody EpisodeRequest request) {
        return ApiResponse.<EpisodeResponse>builder()
                .result(episodeService.create(request))
                .build();
    }

    @Operation(summary = "Lấy danh sách tập phim theo bộ phim")
    @GetMapping("/movie/{movieId}")
    ApiResponse<List<EpisodeResponse>> getByMovie(@PathVariable String movieId) {
        return ApiResponse.<List<EpisodeResponse>>builder()
                .result(episodeService.getByMovieId(movieId))
                .build();
    }

    @Operation(summary = "Cập nhật tập phim")
    @PutMapping("/{id}")
    ApiResponse<EpisodeResponse> update(@PathVariable String id, @RequestBody EpisodeRequest request) {
        return ApiResponse.<EpisodeResponse>builder()
                .result(episodeService.update(id, request))
                .build();
    }

    @Operation(summary = "Xóa tập phim")
    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id) {
        episodeService.delete(id);
        return ApiResponse.<Void>builder().build();
    }
}
