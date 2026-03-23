package com.example.movie_backend.service;

import com.example.movie_backend.dto.request.StudioRequest;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.dto.response.StudioResponse;

import java.util.List;

public interface StudioService {
    StudioResponse create(StudioRequest request);

    List<StudioResponse> getAll();

    PageResponse<StudioResponse> getAllPaginated(int page, int size);

    StudioResponse getById(String id);

    StudioResponse update(String id, StudioRequest request);

    void delete(String id);
}
