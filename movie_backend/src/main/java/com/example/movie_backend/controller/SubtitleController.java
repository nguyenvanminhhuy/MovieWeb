package com.example.movie_backend.controller;

import com.example.movie_backend.dto.request.SubtitleRequest;
import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.SubtitleResponse;
import com.example.movie_backend.service.SubtitleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/subtitles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Subtitle Controller", description = "Quản lý phụ đề cho tập phim (Admin)")
@PreAuthorize("hasRole('ADMIN')")
public class SubtitleController {
    SubtitleService subtitleService;

    @Operation(summary = "Thêm phụ đề mới")
    @PostMapping
    ApiResponse<SubtitleResponse> create(@RequestBody @Valid SubtitleRequest request) {
        return ApiResponse.<SubtitleResponse>builder()
                .result(subtitleService.create(request))
                .build();
    }

    @Operation(summary = "Lấy danh sách phụ đề theo tập phim")
    @GetMapping("/episode/{episodeId}")
    ApiResponse<List<SubtitleResponse>> getByEpisode(@PathVariable String episodeId) {
        return ApiResponse.<List<SubtitleResponse>>builder()
                .result(subtitleService.getByEpisodeId(episodeId))
                .build();
    }

    @Operation(summary = "Xóa phụ đề")
    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id) {
        subtitleService.delete(id);
        return ApiResponse.<Void>builder().build();
    }
}