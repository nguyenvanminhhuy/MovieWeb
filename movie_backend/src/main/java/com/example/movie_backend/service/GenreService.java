package com.example.movie_backend.service;

import com.example.movie_backend.dto.request.GenreRequest;
import com.example.movie_backend.dto.response.GenreResponse;
import com.example.movie_backend.dto.response.PageResponse;

import java.util.List;

public interface GenreService {
    GenreResponse create(GenreRequest request);

    List<GenreResponse> getAll();

    PageResponse<GenreResponse> getAllPaginated(int page, int size);

    GenreResponse getById(String id);

    GenreResponse update(String id, GenreRequest request);

    void delete(String id);
}
