package com.example.movie_backend.controller;

import com.example.movie_backend.dto.response.*;
import com.example.movie_backend.service.*;
import com.example.movie_backend.enums.MovieStatus;
import com.example.movie_backend.enums.MovieType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Common Controller", description = "Các API công khai dành cho khách (không cần đăng nhập)")
public class CommonController {
    MovieService movieService;
    GenreService genreService;
    StudioService studioService;
    EpisodeService episodeService;
    VideoSourceService videoSourceService;
    SubtitleService subtitleService;
    CommentService commentService;
    ReviewService reviewService;

    @Operation(summary = "Tìm kiếm và lọc phim nâng cao")
    @GetMapping("/movies/search")
    ApiResponse<PageResponse<MovieResponse>> searchMovies(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "genreId", required = false) String genreId,
            @RequestParam(value = "franchiseId", required = false) String franchiseId,
            @RequestParam(value = "type", required = false) MovieType type,
            @RequestParam(value = "status", required = false) MovieStatus status,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .result(movieService.search(query, genreId, franchiseId, type, status, page, size))
                .build();
    }

    @Operation(summary = "Lấy danh sách bình luận theo phim")
    @GetMapping("/comments/movie/{movieId}")
    ApiResponse<List<CommentResponse>> getCommentsByMovie(@PathVariable String movieId) {
        return ApiResponse.<List<CommentResponse>>builder()
                .result(commentService.getByMovieId(movieId))
                .build();
    }

    @Operation(summary = "Lấy danh sách đánh giá theo phim")
    @GetMapping("/reviews/movie/{movieId}")
    ApiResponse<List<ReviewResponse>> getReviewsByMovie(@PathVariable String movieId) {
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(reviewService.getByMovieId(movieId))
                .build();
    }

    @Operation(summary = "Lấy danh sách phim")
    @GetMapping("/movies")
    ApiResponse<PageResponse<MovieResponse>> getAllMovies(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .result(movieService.getAll(page, size))
                .build();
    }

    @Operation(summary = "Lấy thông tin phim theo ID")
    @GetMapping("/movies/{id}")
    ApiResponse<MovieResponse> getMovieById(@PathVariable String id) {
        return ApiResponse.<MovieResponse>builder()
                .result(movieService.getById(id))
                .build();
    }

    @Operation(summary = "Lấy danh sách thể loại")
    @GetMapping("/genres")
    ApiResponse<PageResponse<GenreResponse>> getAllGenres(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<GenreResponse>>builder()
                .result(genreService.getAllPaginated(page, size))
                .build();
    }

    @Operation(summary = "Lấy danh sách studio")
    @GetMapping("/studios")
    ApiResponse<PageResponse<StudioResponse>> getAllStudios(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<StudioResponse>>builder()
                .result(studioService.getAllPaginated(page, size))
                .build();
    }

    @Operation(summary = "Lấy danh sách tập phim theo bộ phim")
    @GetMapping("/episodes/movie/{movieId}")
    ApiResponse<List<EpisodeResponse>> getEpisodesByMovie(@PathVariable String movieId) {
        return ApiResponse.<List<EpisodeResponse>>builder()
                .result(episodeService.getByMovieId(movieId))
                .build();
    }

    @Operation(summary = "Lấy danh sách nguồn video theo tập phim")
    @GetMapping("/video-sources/episode/{episodeId}")
    ApiResponse<List<VideoSourceResponse>> getVideoSourcesByEpisode(@PathVariable String episodeId) {
        return ApiResponse.<List<VideoSourceResponse>>builder()
                .result(videoSourceService.getByEpisodeId(episodeId))
                .build();
    }

    @Operation(summary = "Lấy danh sách phụ đề theo tập phim")
    @GetMapping("/subtitles/episode/{episodeId}")
    ApiResponse<List<SubtitleResponse>> getSubtitlesByEpisode(@PathVariable String episodeId) {
        return ApiResponse.<List<SubtitleResponse>>builder()
                .result(subtitleService.getByEpisodeId(episodeId))
                .build();
    }

    @Operation(summary = "Tăng lượt xem cho phim")
    @PostMapping("/movies/{id}/views")
    ApiResponse<Void> incrementMovieViews(@PathVariable String id) {
        movieService.incrementViews(id);
        return ApiResponse.<Void>builder().build();
    }

    @Operation(summary = "Lấy top phim (day, week, month, all)")
    @GetMapping("/movies/top")
    ApiResponse<PageResponse<MovieResponse>> getTopMovies(
            @RequestParam(value = "type", required = false, defaultValue = "all") String type,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .result(movieService.getTopMovies(type, page, size))
                .build();
    }

    @Operation(summary = "Lấy danh sách phim liên quan")
    @GetMapping("/movies/{id}/related")
    ApiResponse<PageResponse<MovieResponse>> getRelatedMovies(
            @PathVariable String id,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size) {
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .result(movieService.getRelatedMovies(id, page, size))
                .build();
    }
}
