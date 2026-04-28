package com.example.movie_backend.service;

import com.example.movie_backend.dto.request.CommentRequest;
import com.example.movie_backend.dto.response.CommentResponse;
import com.example.movie_backend.dto.response.PageResponse;

import java.util.List;

public interface CommentService {
    CommentResponse create(CommentRequest request);
    List<CommentResponse> getByMovieId(String movieId);
    void delete(String id);
    CommentResponse likeComment(String id);
    PageResponse<CommentResponse> getAll(int page, int size);
}
