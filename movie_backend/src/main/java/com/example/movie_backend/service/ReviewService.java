package com.example.movie_backend.service;

import com.example.movie_backend.dto.request.ReviewRequest;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse create(ReviewRequest request);
    List<ReviewResponse> getByMovieId(String movieId);
    void delete(String id);
    PageResponse<ReviewResponse> getAll(int page, int size);
}
