package com.example.movie_backend.controller;

import com.example.movie_backend.dto.request.VideoSourceRequest;
import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.VideoSourceResponse;
import com.example.movie_backend.service.VideoSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/video-sources")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Video Source Controller", description = "Quản lý nguồn video cho tập phim (Admin)")
@PreAuthorize("hasRole('ADMIN')")
public class VideoSourceController {
    VideoSourceService videoSourceService;

    @Operation(summary = "Thêm nguồn video mới")
    @PostMapping
    ApiResponse<VideoSourceResponse> create(@RequestBody VideoSourceRequest request) {
        return ApiResponse.<VideoSourceResponse>builder()
                .result(videoSourceService.create(request))
                .build();
    }

    @Operation(summary = "Lấy danh sách nguồn video theo tập phim")
    @GetMapping("/episode/{episodeId}")
    ApiResponse<List<VideoSourceResponse>> getByEpisode(@PathVariable String episodeId) {
        return ApiResponse.<List<VideoSourceResponse>>builder()
                .result(videoSourceService.getByEpisodeId(episodeId))
                .build();
    }

    @Operation(summary = "Xóa nguồn video")
    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id) {
        videoSourceService.delete(id);
        return ApiResponse.<Void>builder().build();
    }
}
